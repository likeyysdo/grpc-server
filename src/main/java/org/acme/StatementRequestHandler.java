package org.acme;

import io.grpc.stub.StreamObserver;
import io.quarkus.remote.ClientStatus;
import io.quarkus.remote.ServerStatus;
import io.quarkus.remote.SimpleStatementRequest;
import io.quarkus.remote.SimpleStatementResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.acme.statement.Session;
import org.acme.statement.State;
import org.jboss.logging.Logger;

/**
 * @Classname StatementRequestHandler
 * @Description TODO
 * @Date 2022/7/17 10:44
 * @Created by byco
 */
public class StatementRequestHandler
    extends  AbstractStatementHandler {

    private static final Logger log = Logger.getLogger(StatementRequestHandler.class);

    public StatementRequestHandler(Connection connection,
        StreamObserver<SimpleStatementResponse> responseObserver) {
        super(responseObserver);
        this.connection = connection;
        session = new Session(connection);
    }

    private Connection connection;
    private Session session;


    @Override
    public void onNext(SimpleStatementRequest simpleStatementRequest) {
        try {
            responseObserver.onNext(session.doAction(simpleStatementRequest));
        } catch (SQLException | IOException e) {
            log.warn(e);
            responseObserver.onNext(session.exceptionHandler(e));
            try {
                session.closeAll();
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.warn("onError");
        try {
            session.closeAll();
        } catch (SQLException e) {
            log.error(e);
        }
        log.error(throwable);
    }

    @Override
    public void onCompleted() {
        log.debug("onCompleted");
        responseObserver.onCompleted();
        try {
            session.closeAll();
        } catch (SQLException e) {
            log.error(e);
        }
    }


}






