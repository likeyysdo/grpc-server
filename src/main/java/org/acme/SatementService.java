package org.acme;

import com.google.protobuf.ByteString;
import io.agroal.api.AgroalDataSource;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.quarkus.remote.ClientStatus;
import io.quarkus.remote.MutinySimpleStatementGrpc;
import io.quarkus.remote.ServerStatus;
import io.quarkus.remote.SimpleStatement;
import io.quarkus.remote.SimpleStatementGrpc;
import io.quarkus.remote.SimpleStatementRequest;
import io.quarkus.remote.SimpleStatementResponse;
import java.sql.SQLException;
import javax.inject.Inject;
import org.jboss.logging.Logger;


/**
 * @Classname SatementService
 * @Description TODO
 * @Date 2022/7/14 16:09
 * @Created by byco
 */
@GrpcService
public class SatementService extends SimpleStatementGrpc.SimpleStatementImplBase {

    private static final Logger log = Logger.getLogger(SatementService.class);

    @Inject
    AgroalDataSource dataSource;


    @Override
    public io.grpc.stub.StreamObserver<io.quarkus.remote.SimpleStatementRequest> exec(
        io.grpc.stub.StreamObserver<io.quarkus.remote.SimpleStatementResponse> responseObserver) {
        try {
            return new StatementRequestHandler(dataSource.getConnection(), responseObserver);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
