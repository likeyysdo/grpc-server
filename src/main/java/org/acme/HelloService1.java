package org.acme;

import io.grpc.stub.StreamObserver;
import io.quarkus.example.Greeter;
import io.quarkus.example.GreeterGrpc;
import io.quarkus.example.HelloReply;
import io.quarkus.example.HelloRequest;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

/**
 * @Classname HelloService
 * @Description TODO
 * @Date 2022/6/29 11:47
 * @Created by byco
 */
@GrpcService
public class HelloService1  extends GreeterGrpc.GreeterImplBase{

    private static final Logger log = Logger.getLogger(HelloService1.class);

    /**
     * <pre>
     *  Sends a greeting
     * </pre>
     *
     * @param request
     */
   @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        log.info(request.getName());
        String name = request.getName();
        String message = "Hello123 " + name;
        responseObserver.onNext(HelloReply.newBuilder().setMessage(message).build());
        responseObserver.onCompleted();
    }

}
