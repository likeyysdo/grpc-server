package org.acme;

import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.spi.DatabaseMetadata;
import java.sql.DriverManager;
import java.util.Arrays;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

/**
 * @Classname ExampleResourceTest
 * @Description TODO
 * @Date 2022/7/15 16:23
 * @Created by byco
 */
@QuarkusTest
class ExampleResourceTest {
    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;


    @Test
    void data() throws InterruptedException {

        //DatabaseMetadata meta = client.getConnectionAndAwait().databaseMetadata();
        //System.out.println(meta.productName());
        client.getConnectionAndAwait();
         client.getConnection().subscribe().with(x->
         {
             x.databaseMetadata();
         });

        Uni<RowSet<Row>> rowSet = client.query("SELECT * FROM BUNDLE").execute();

        rowSet
            .onItem().transformToMulti(set -> Multi.createFrom().iterable(set));


        client.query("SELECT * FROM BUNDLE1").execute()

            .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
            .subscribe().with(x->{
                System.out.println(x.getString(0));
            });
    }


    @Test
    void fun1() {
        DatabaseMetadata meta = client.getConnectionAndAwait().databaseMetadata();
        System.out.println(meta.productName());
        System.out.println("start");
        Uni<RowSet<Row>> rowSet = client.query("SELECT * FROM BUNDLE").execute();
        Multi<Row> rows =  rowSet.onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
            .onSubscription()
            .invoke(() -> System.out.println("⬇️ Subscribed"))
            .onItem()
            .invoke(i -> System.out.println("⬇️ Received item: " + i))
            .onFailure()
            .invoke(f -> System.out.println("⬇️ Failed with " + f))
            .onCompletion()
            .invoke(() -> System.out.println("⬇️ Completed"))
            .onCancellation()
            .invoke(() -> System.out.println("⬆️ Cancelled"))
            .onRequest()
            .invoke(l -> System.out.println("⬆️ Requested: " + l));

        rows.subscribe().with(x->{
                System.out.println(x.getString(1));
            } , Throwable::printStackTrace);
        System.out.println("end");
    }

    public static void main(String[] args) {
        Uni.createFrom().item("hello")
            .onSubscription()
            .invoke(() -> System.out.println("⬇️ Subscribed"))
            .onItem()
            .invoke(i -> System.out.println("⬇️ Received item: " + i))
            .onFailure()
            .invoke(f -> System.out.println("⬇️ Failed with " + f))

            .onCancellation()
            .invoke(() -> System.out.println("⬆️ Cancelled"))

            .onItem().transform(item -> item + " mutiny")
            .onItem().transform(String::toUpperCase)
            .subscribe().with(item -> System.out.println(">> " + item));
    }

}