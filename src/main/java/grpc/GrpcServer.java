package grpc;

import java.io.IOException;

public interface GrpcServer {

    void init(Integer port);

    void startServer() throws IOException;

    void stopServer();

    void awaitTermination() throws InterruptedException;
}
