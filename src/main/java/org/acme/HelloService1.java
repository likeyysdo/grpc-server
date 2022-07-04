package org.acme;

import io.grpc.stub.StreamObserver;
import io.quarkus.example.Greeter;
import io.quarkus.example.GreeterGrpc;
import io.quarkus.example.HelloReply;
import io.quarkus.example.HelloRequest;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;

/**
 * @Classname HelloService
 * @Description TODO
 * @Date 2022/6/29 11:47
 * @Created by byco
 */
//@GrpcService   extends GreeterGrpc.GreeterImplBase
public class HelloService1  {
    /**
     * <pre>
     *  Sends a greeting
     * </pre>
     *
     * @param request
     */
   //@Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        String name = request.getName();
        String message = "Hello " + name;
        responseObserver.onNext(HelloReply.newBuilder().setMessage(message).build());
        responseObserver.onCompleted();
    }

}
