package com.lncn.rsql;

import com.google.protobuf.ByteString;
import com.google.protobuf.UnsafeByteOperations;
import com.lncn.rsql.cache.RCache;
import com.lncn.rsql.config.RsqlConfig;
import com.lncn.rsql.remotejdbc.encode.ResultRowEncodeFactory;
import com.lncn.rsql.remotejdbc.metadata.DefaultResultSetMetaDataEncoder;
import com.lncn.rsql.utils.CodeUtils;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @Classname Session
 * @Description TODO
 * @Date 2022/7/17 11:30
 * @Created by byco
 */


public class Session {
    private static final Logger log = LoggerFactory.getLogger(Session.class);
    private RsqlConfig defaultConfig;
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


    //cache
    private boolean enableCache;
    private RCache cache;
    private String cacheKey;
    private boolean firstBatch;
    private boolean hasCache;
    private ByteString[] cacheValue;

    public Session(Connection connection, RsqlConfig defaultConfig,
                   RCache cache){
        state = State.UNINITIALIZED;
        initialized = false;
        canceled = false;
        closed = false;
        this.connection = connection;
        this.defaultConfig = defaultConfig;
        this.cache = cache;
    }

    public RsqlConfig getConfig(){
        return defaultConfig;
    }

    public SimpleStatementResponse exceptionHandler(Exception e){
        state = State.ERROR;
        return SimpleStatementResponse.newBuilder()
            .setStatus(ServerStatus.SERVER_STATUS_ERROR)
            .setBody(e.getMessage())
            .build();
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

    SimpleStatementResponse initialize() throws IOException {
        log.debug("initialize Properties");
        initializeProperties(request.getBody());
        state = state.doAction(ClientStatus.CLIENT_STATUS_INITIALIZE);
        log.debug("Return Status" + ServerStatus.SERVER_STATUS_INITIALIZED.name());
        initialized = true;
        return SimpleStatementResponse.newBuilder()
            .setStatus(ServerStatus.SERVER_STATUS_INITIALIZED)
            .build();
    }

    void initializeProperties(String encodeProperties) throws IOException {
        log.debug("encodeProperties " + encodeProperties);
        this.properties = CodeUtils.decodeProperties(encodeProperties);
        log.debug("decodeProperties " + properties);
        initializeBuffer();
        initializeCache();
    }

    void initializeBuffer(){
        log.debug("initializeBuffer");
        int bufferSize = defaultConfig.defaultFetchSize() == 0 ? 1000 : defaultConfig.defaultFetchSize();
        String clientFetchSizeText = getNotNullProperty("fetchSize");
        log.debug("Properties bufferSize client value :{}" , clientFetchSizeText);
        if( isNotEmptyPropertyValue(clientFetchSizeText) ){
            int clientFetchSize = Integer.parseInt(clientFetchSizeText);
            if( defaultConfig.minFetchSize() <= clientFetchSize && clientFetchSize <=
                defaultConfig.maxFetchSize() ){
                bufferSize = clientFetchSize;
            }
        }
        log.debug("final bufferSize  value :" + bufferSize);
        buffer = new ByteString[bufferSize];
    }

    void initializeCache(){
        log.debug("initializeCache");
        String isEnableCacheText = getNotNullProperty("enableCache");
        log.debug("Properties isEnableCacheText :{}" , isEnableCacheText);
        if( "true".equals(isEnableCacheText) || "1".equals(isEnableCacheText)){
            this.enableCache = true;
            this.firstBatch = true;
        }else{
            this.enableCache = false;
        }
    }

    boolean isNotEmptyPropertyValue(String property){
        return !property.isEmpty();
    }

    String getNotNullProperty( String property ){
        if( properties == null ) return "";
        String propertyValue = properties.getProperty(property);
        if( propertyValue != null && !propertyValue.trim().isBlank() ){
            return propertyValue;
        }else{
            return "";
        }
    }


    SimpleStatementResponse sendStatement() throws SQLException, IOException {
            log.debug("encode sql body " + request.getBody());
            String sql = CodeUtils.decodeText(request.getBody());
            log.info("executeQuery " + sql);
            if( enableCache ){
                cacheKey = sql;
                log.debug("cache key:{}",cacheKey);

                Object value = cache.getIfPresent(cacheKey);
                if( value == null ){
                    log.debug("cache key:{} not aimed turn to direct query",cacheKey);
                    this.hasCache = false;
                }else {
                    this.hasCache = true;
                    log.debug("cache key:{} aimed ",cacheKey);
                    Object[] valueArray = (Object[])value;
                    resultSetMetaData = (byte[]) valueArray[0];
                    this.cacheValue = (ByteString[]) valueArray[1];
                    log.debug("Return Status {}" , ServerStatus.SERVER_STATUS_RECEIVED_STATEMENT.name());
                    state = state.doAction(ClientStatus.CLIENT_STATUS_SEND_STATEMENT);
                    return SimpleStatementResponse.newBuilder()
                        .setStatus(ServerStatus.SERVER_STATUS_RECEIVED_STATEMENT)
                        .addResult(UnsafeByteOperations.unsafeWrap(resultSetMetaData))
                        .build();
                }
            }
            statement =  connection.createStatement();
            resultSet = statement.executeQuery(sql);
            ResultSetMetaData sourceMetaData = resultSet.getMetaData();

            resultSetMetaData = new DefaultResultSetMetaDataEncoder().encode(sourceMetaData);
            factory = new ResultRowEncodeFactory.Builder(sourceMetaData).build();
            log.debug("Return Status {}" , ServerStatus.SERVER_STATUS_RECEIVED_STATEMENT.name());
            state = state.doAction(ClientStatus.CLIENT_STATUS_SEND_STATEMENT);
            return SimpleStatementResponse.newBuilder()
                .setStatus(ServerStatus.SERVER_STATUS_RECEIVED_STATEMENT)
                .addResult(UnsafeByteOperations.unsafeWrap(resultSetMetaData))
                .build();

    }

    SimpleStatementResponse receiveData() throws SQLException, IOException {
        if( enableCache && hasCache ){
            return receiveCachedData();
        }else{
            return receiveDirectData();
        }
    }




    SimpleStatementResponse receiveCachedData() throws SQLException, IOException {
        if( !state.equals(State.STATEMENT_PREPARED) ){
            //cache aimed query should have only once receive data action
            throw new IllegalStateException("cached query in received data action but in wrong state: " + state.name());
        }
        state = State.TRANSMIT_FINISHED;
        SimpleStatementResponse.Builder builder = SimpleStatementResponse.newBuilder()
            .setStatus(ServerStatus.SERVER_STATUS_NOT_HAS_NEXT_DATA);
        ByteString[] result = this.cacheValue;
        for (ByteString bytes : result) {
            builder.addResult(bytes);
        }
        log.debug("cached value return status:{}" , ServerStatus.SERVER_STATUS_NOT_HAS_NEXT_DATA.name());
        return builder.build();
    }

    SimpleStatementResponse receiveDirectData() throws SQLException, IOException {
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
        //log.debug("real fetch size " +count);
        if( state.equals(State.STATEMENT_PREPARED) ){
            state = state.doAction(ClientStatus.CLIENT_STATUS_RECEIVE_DATA);
        }

        //cache operation
        if( enableCache  && firstBatch && state.equals(State.TRANSMIT_FINISHED) ){
            //add cache
            ByteString[] cacheRowValue = new ByteString[count];
            for( int i = 0 ; i < count ; i++){
                cacheRowValue[i] = UnsafeByteOperations.unsafeWrap(buffer[i].toByteArray());
            }
            Object[] cacheValue = new Object[]{Arrays.copyOf(this.resultSetMetaData,this.resultSetMetaData.length)
                ,cacheRowValue };
            cache.put(cacheKey,cacheValue);
            log.debug("cache put key:{} value size:{}",cacheKey,cacheRowValue.length);
        }
        firstBatch = false;


        if( state.equals(State.TRANSMITTING)  ){
            SimpleStatementResponse.Builder builder = SimpleStatementResponse.newBuilder()
                .setStatus(ServerStatus.SERVER_STATUS_HAS_NEXT_DATA);
            for( int i = 0 ; i < count ; i++){
                builder.addResult(buffer[i]);
            }
            log.debug("Return Status {}" , ServerStatus.SERVER_STATUS_HAS_NEXT_DATA.name());
            return builder.build();
        }else if(state.equals(State.TRANSMIT_FINISHED)){
            SimpleStatementResponse.Builder builder = SimpleStatementResponse.newBuilder()
                .setStatus(ServerStatus.SERVER_STATUS_NOT_HAS_NEXT_DATA);
            for( int i = 0 ; i < count ; i++){
                //log.debug("buffer isnull " + i +" "+  (buffer[i] == null));
                builder.addResult(buffer[i]);
            }
            log.debug("Return Status {}" , ServerStatus.SERVER_STATUS_NOT_HAS_NEXT_DATA.name());
            return builder.build();
        }

        throw new IllegalStateException("transmitting data but in wrong state: " + state.name());

    }
    SimpleStatementResponse finished() throws SQLException {
        closeAll();
        log.debug("Return Status {}" ,ServerStatus.SERVER_STATUS_FINISHED.name());
        return SimpleStatementResponse.newBuilder()
            .setStatus(ServerStatus.SERVER_STATUS_FINISHED)
            .build();
    }
    SimpleStatementResponse unknown() throws SQLException {
        closeAll();
        log.debug("Return Status {}" ,ServerStatus.SERVER_STATUS_ERROR.name());
        return SimpleStatementResponse.newBuilder()
            .setStatus(ServerStatus.SERVER_STATUS_ERROR)
            .setBody("Receive client Error status")
            .build();
    }
    SimpleStatementResponse cancel() throws SQLException {
        if( !canceled ) {
            if( statement  == null ){
                log.debug("statement is null do not need cancel");
            }else{
                statement.cancel();
            }
            canceled = true;
            closeStatement();
            state = State.CANCELED;
        }
        log.debug("Return Status {}" , ServerStatus.SERVER_STATUS_CANCELED.name());
        return SimpleStatementResponse.newBuilder()
            .setStatus(ServerStatus.SERVER_STATUS_CANCELED)
            .build();
    }
    SimpleStatementResponse error() throws SQLException {
        closeAll();
        log.debug("Return Status {}" , ServerStatus.SERVER_STATUS_ERROR.name());
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
