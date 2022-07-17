package org.acme.statement;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.UnsafeByteOperations;
import io.agroal.api.AgroalDataSource;
import io.quarkus.remote.ClientStatus;
import io.quarkus.remote.ServerStatus;
import io.quarkus.remote.SimpleStatementRequest;
import io.quarkus.remote.SimpleStatementResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.acme.SatementService;
import org.acme.remotejdbc.encode.ResultRowEncodeFactory;
import org.acme.remotejdbc.metadata.DefaultResultSetMetaDataEncoder;
import org.jboss.logging.Logger;

/**
 * @Classname Session
 * @Description TODO
 * @Date 2022/7/17 11:30
 * @Created by byco
 */

public class Session {
    private static final Logger log = Logger.getLogger(Session.class);

    private Properties properties;
    private State state;
    private boolean canceled;
    private boolean closed;
    private boolean initialized;

    private SimpleStatementRequest request;
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private ByteString[] buffer;

    private ResultRowEncodeFactory factory;
    private byte[] resultSetMetaData;

    public Session(Connection connection){
        state = State.UNINITIALIZED;
        initialized = false;
        canceled = false;
        closed = false;
        this.connection = connection;
    }

    public SimpleStatementResponse doAction(SimpleStatementRequest simpleStatementRequest)
        throws SQLException, IOException {
        request = simpleStatementRequest;
        ClientStatus action = request.getStatus();
        if(!state.check(action)){
            throw new IllegalStateException(state,action);
        }
        switch (action){
            case CLIENT_STATUS_INITIALIZE : return initialize();
            case CLIENT_STATUS_SEND_STATEMENT : return sendStatement();
            case CLIENT_STATUS_RECEIVE_DATA : return receiveData();
            case CLIENT_STATUS_FINISHED : return finished();
            case CLIENT_STATUS_UNKNOWN : return unknown();
            case CLIENT_STATUS_CANCEL : return cancel();
            case CLIENT_STATUS_ERROR : return error();
            default: throw new IllegalStateException("Unknown ClientStatus");
        }
    }

    SimpleStatementResponse initialize() throws SQLException {
        initialized = true;
        buffer = new ByteString[500];
        state = state.doAction(ClientStatus.CLIENT_STATUS_INITIALIZE);

        return SimpleStatementResponse.newBuilder()
            .setStatus(ServerStatus.SERVER_STATUS_INITIALIZED)
            .build();
    }
    SimpleStatementResponse sendStatement() throws SQLException, IOException {
       statement =  connection.createStatement();
       resultSet = statement.executeQuery(request.getBody());
       ResultSetMetaData sourceMetaData = resultSet.getMetaData();

       resultSetMetaData = new DefaultResultSetMetaDataEncoder().encode(sourceMetaData);
       factory = new ResultRowEncodeFactory.Builder(sourceMetaData).build();

       state = state.doAction(ClientStatus.CLIENT_STATUS_SEND_STATEMENT);
       return SimpleStatementResponse.newBuilder()
            .setStatus(ServerStatus.SERVER_STATUS_RECEIVED_STATEMENT)
            .addResult(UnsafeByteOperations.unsafeWrap(resultSetMetaData))
            .build();
    }
    SimpleStatementResponse receiveData() throws SQLException, IOException {
        int count = 0;
        boolean fetchBreak = false;
        while(resultSet.next()){
            factory.add(resultSet);
            buffer[count++] = UnsafeByteOperations.unsafeWrap(factory.encode());
            if( count >= buffer.length ){
                fetchBreak = true;
                break;
            }
        }
        if( !fetchBreak ){
            //finish
            state = State.TRANSMIT_FINISHED;
        }
        log.debug("buffer size" +count);
        if( state.equals(State.TRANSMITTING) ){
            SimpleStatementResponse.Builder builder = SimpleStatementResponse.newBuilder()
                .setStatus(ServerStatus.SERVER_STATUS_HAS_NEXT_DATA);
            for( int i = 0 ; i < count ; i++){
                builder.addResult(buffer[i]);
            }
            return builder.build();
        }else if(state.equals(State.TRANSMIT_FINISHED)){
            SimpleStatementResponse.Builder builder = SimpleStatementResponse.newBuilder()
                .setStatus(ServerStatus.SERVER_STATUS_NOT_HAS_NEXT_DATA);
            for( int i = 0 ; i < count ; i++){
                log.debug("buffer isnull " + i +" "+  (buffer[i] == null));
                builder.addResult(buffer[i]);
            }
            return builder.build();
        }

        throw new IllegalStateException("transmitting data but in wrong state: " + state.name());

    }
    SimpleStatementResponse finished() throws SQLException {
        closeAll();
        return SimpleStatementResponse.newBuilder()
            .setStatus(ServerStatus.SERVER_STATUS_FINISHED)
            .build();
    }
    SimpleStatementResponse unknown() throws SQLException {
        closeAll();
        return SimpleStatementResponse.newBuilder()
            .setStatus(ServerStatus.SERVER_STATUS_ERROR)
            .setBody("Receive client Error status")
            .build();
    }
    SimpleStatementResponse cancel() throws SQLException {
        statement.cancel();
        canceled = true;
        closeStatement();
        state = State.CANCELED;
        return SimpleStatementResponse.newBuilder()
            .setStatus(ServerStatus.SERVER_STATUS_FINISHED)
            .build();
    }
    SimpleStatementResponse error() throws SQLException {
        closeAll();
        return SimpleStatementResponse.newBuilder()
            .setStatus(ServerStatus.SERVER_STATUS_ERROR)
            .setBody("Receive client Error status")
            .build();
    }

    void closeResultSet() throws SQLException {
        if( resultSet != null ){
            resultSet.close();
        }
    }

    void closeStatement() throws SQLException {
        closeResultSet();
        if( statement != null ){
            statement.close();
        }
    }
    void closeConnection() throws SQLException {
        closeStatement();
        if( connection != null ){
            connection.close();
        }
    }

    public void closeAll() throws SQLException {
         closeConnection();
    }

}
