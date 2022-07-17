package org.acme;

import io.grpc.stub.StreamObserver;
import io.quarkus.remote.SimpleStatementRequest;
import io.quarkus.remote.SimpleStatementResponse;

/**
 * @Classname AbsrtactStatementHandler
 * @Description TODO
 * @Date 2022/7/17 10:53
 * @Created by byco
 */
public abstract class AbstractStatementHandler implements StreamObserver<SimpleStatementRequest> {

    protected StreamObserver<SimpleStatementResponse> responseObserver;

    public AbstractStatementHandler(
        StreamObserver<SimpleStatementResponse> responseObserver) {
        this.responseObserver = responseObserver;
    }
}
