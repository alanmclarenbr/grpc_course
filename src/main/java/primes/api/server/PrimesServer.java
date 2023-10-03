package primes.api.server;

import grpc.AbstractGrpcServer;
import io.grpc.BindableService;

public class PrimesServer extends AbstractGrpcServer {

    @Override
    protected BindableService getBindableService() {
        return new PrimesServerImpl();
    }
}
