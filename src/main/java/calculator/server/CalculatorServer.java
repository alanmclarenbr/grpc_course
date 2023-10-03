package calculator.server;

import grpc.AbstractGrpcServer;
import io.grpc.BindableService;

public class CalculatorServer extends AbstractGrpcServer {

    @Override
    protected BindableService getBindableService() {
        return new CalculatorServerImpl();
    }
}
