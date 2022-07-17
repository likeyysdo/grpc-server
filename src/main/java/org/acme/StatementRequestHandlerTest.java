package org.acme;

import io.grpc.stub.StreamObserver;
import io.quarkus.remote.ServerStatus;
import io.quarkus.remote.SimpleStatementRequest;
import io.quarkus.remote.SimpleStatementResponse;
import org.jboss.logging.Logger;

/**
 * @Classname StatementRequestHandlerTest
 * @Description TODO
 * @Date 2022/7/17 10:55
 * @Created by byco
 */
public class StatementRequestHandlerTest extends  AbstractStatementHandler{
    public StatementRequestHandlerTest(
        StreamObserver<SimpleStatementResponse> responseObserver) {
        super(responseObserver);
    }

    private static final Logger log = Logger.getLogger(StatementRequestHandlerTest.class);

    int i = 0;

    @Override
    public void onNext(SimpleStatementRequest simpleStatementRequest) {
        log.info("Incoming Message" + simpleStatementRequest.getBody());
        SimpleStatementResponse response = SimpleStatementResponse.newBuilder().setStatus(
                ServerStatus.SERVER_STATUS_INITIALIZED)
            .setBody("不会吧不会吧")
            .build();
        responseObserver.onNext(response);
        response = SimpleStatementResponse.newBuilder().setStatus(ServerStatus.SERVER_STATUS_FINISHED)
            .setBody("寄了寄了" + i++)
            .build();
        responseObserver.onNext(response);
        //responseObserver.onCompleted();
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("onNext");
    }

    @Override
    public void onCompleted() {
        log.info("onCompleted");
    }
}
