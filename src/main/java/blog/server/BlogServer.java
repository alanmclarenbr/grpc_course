package blog.server;

import com.mongodb.client.MongoClient;
import grpc.AbstractGrpcServer;
import io.grpc.BindableService;

public class BlogServer extends AbstractGrpcServer {

    private final MongoClient mongoClient;

    public BlogServer(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    protected BindableService getBindableService() {
        return new BlogServerImpl(mongoClient);
    }
}
