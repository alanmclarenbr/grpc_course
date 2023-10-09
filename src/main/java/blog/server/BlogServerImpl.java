package blog.server;

import com.blog.Blog;
import com.blog.BlogId;
import com.blog.BlogServiceGrpc;
import com.google.protobuf.Empty;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static io.grpc.Status.INTERNAL;
import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;

public class BlogServerImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private static final String AUTHOR = "author";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private final MongoCollection<Document> mongoCollection;

    BlogServerImpl(MongoClient mongoClient) {
        MongoDatabase db = mongoClient.getDatabase("blogdb");
        mongoCollection = db.getCollection("blog");
    }

    @Override
    public void createBlog(Blog request, StreamObserver<BlogId> responseObserver) {
        Document doc = new Document(AUTHOR, request.getAuthor())
                .append(TITLE, request.getTitle())
                .append(CONTENT, request.getContent());

        InsertOneResult result;
        try {
            result = mongoCollection.insertOne(doc);
        } catch (Exception e) {
            responseObserver.onError(INTERNAL
                    .withDescription(e.getLocalizedMessage())
                    .asRuntimeException());
            return;
        }

        if (!result.wasAcknowledged()) {
            responseObserver.onError(INTERNAL
                    .withDescription("Blog couldn't be created.")
                    .asRuntimeException());
            return;
        }

        String id = Objects.requireNonNull(result.getInsertedId()).asObjectId().getValue().toString();

        responseObserver.onNext(BlogId.newBuilder().setId(id).build());
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(BlogId request, StreamObserver<Blog> responseObserver) {
        if (request.getId() == null || request.getId().isEmpty()) {
            responseObserver.onError(INVALID_ARGUMENT
                    .withDescription("The blog ID cannot be empty.")
                    .asRuntimeException());
            return;
        }

        String id = request.getId();
        Document result = mongoCollection.find(eq("_id", new ObjectId(id))).first();

        if (result == null) {
            responseObserver.onError(NOT_FOUND
                    .withDescription("Blog was not found.")
                    .augmentDescription("BlogId: " + id)
                    .asRuntimeException());
            return;
        }

        responseObserver.onNext(Blog.newBuilder()
                .setAuthor(result.getString(AUTHOR))
                .setTitle(result.getString(TITLE))
                .setContent(result.getString(CONTENT))
                .build());

        responseObserver.onCompleted();
    }

    @Override
    public void updateBlog(Blog request, StreamObserver<Empty> responseObserver) {
        Document updated;
        String id = request.getId();
        updated = mongoCollection.findOneAndUpdate(
                eq("_id", new ObjectId(id)),
                combine(
                        set("author", request.getAuthor()),
                        set("title", request.getTitle()),
                        set("content", request.getContent())
                ));

        if (updated == null) {
            responseObserver.onError(NOT_FOUND
                    .withDescription("Blog couldn't be found.")
                    .asRuntimeException());
            return;
        }

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBlog(BlogId request, StreamObserver<Empty> responseObserver) {
        if (request.getId() == null || request.getId().isEmpty()) {
            responseObserver.onError(INVALID_ARGUMENT
                    .withDescription("The blog ID cannot be empty.")
                    .asRuntimeException());
            return;
        }

        mongoCollection.deleteOne(eq("_id", new ObjectId(request.getId())));

        responseObserver.onNext(Empty.getDefaultInstance());

        responseObserver.onCompleted();
    }

    @Override
    public void listBlogs(Empty request, StreamObserver<Blog> responseObserver) {
        FindIterable<Document> documents = mongoCollection.find();

        documents.forEach(document ->
            responseObserver.onNext(Blog.newBuilder()
            .setId(document.getObjectId("_id").toString())
            .setAuthor(document.getString("author"))
            .setTitle(document.getString("title"))
            .setContent(document.getString("content"))
            .build())
        );

        responseObserver.onCompleted();
    }
}
