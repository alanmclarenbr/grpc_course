package grpc;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public abstract class AbstractGrpcServer implements GrpcServer {

    private Server server;

    @Override
    public void init(Integer port) {
        if (server == null || server.isTerminated()) {
            server = ServerBuilder.forPort(port)
                    .addService(getBindableService())
                    .addService(ProtoReflectionService.newInstance())
                    .build();
        }
    }

    protected abstract BindableService getBindableService();

    @Override
    public void startServer() throws IOException {
        server.start();
    }

    @Override
    public void stopServer() {
        server.shutdown();
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        server.awaitTermination();
    }
}
