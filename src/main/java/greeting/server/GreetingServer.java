package greeting.server;

import grpc.AbstractGrpcServer;
import io.grpc.BindableService;

public class GreetingServer extends AbstractGrpcServer {

    @Override
    protected BindableService getBindableService() {
        return new GreetingServerImpl();
    }
}
