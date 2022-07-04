package org.acme;

import io.quarkus.example.Greeter;
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
@GrpcService
public class HelloService  implements Greeter {
    /**
     * <pre>
     *  Sends a greeting
     * </pre>
     *
     * @param request
     */
    @Override
    public Uni<HelloReply> sayHello(HelloRequest request) {
        return Uni.createFrom().item(() -> HelloReply.newBuilder()
            .setMessage("hahasha "+ request.getName()).build());
    }
}
