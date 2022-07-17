package org.acme;

import io.quarkus.example.Greeter;
import io.quarkus.example.HelloRequest;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class ExampleResource {

    @GrpcClient
    Greeter hello;

    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @GET
    @Path("/123")
    public void fun1(){
        System.out.println("run");
        Uni<RowSet<Row>> rowSet = client.query("SELECT * FROM BUNDLE").execute();
        rowSet.onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
            .onItem().transform(x->{
                System.out.println("row");
                System.out.println(x.getString(1));
                return  2;
            }).log();
    }


    @GET
    @Path("/name/{name}")
    public Uni<String> hello(String name) {
        return hello.sayHello(HelloRequest.newBuilder().setName(name).build())
            .onItem().transform(helloReply -> helloReply.getMessage());
    }




}