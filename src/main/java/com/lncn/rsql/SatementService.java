package com.lncn.rsql;

import com.lncn.rsql.config.RsqlConfig;
import io.agroal.api.AgroalDataSource;
import io.quarkus.grpc.GrpcService;
import io.quarkus.remote.SimpleStatementGrpc;
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


    @Inject
    RsqlConfig defaultConfig;

    @Override
    public io.grpc.stub.StreamObserver<io.quarkus.remote.SimpleStatementRequest> exec(
        io.grpc.stub.StreamObserver<io.quarkus.remote.SimpleStatementResponse> responseObserver) {
        try {
            return new StatementRequestHandler(dataSource.getConnection(),defaultConfig, responseObserver);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
