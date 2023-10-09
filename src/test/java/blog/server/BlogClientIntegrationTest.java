package blog.server;

import blog.client.BlogClient;
import com.blog.Blog;
import com.blog.BlogServiceGrpc;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import grpc.GrpcServer;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BlogClientIntegrationTest {

    private static final int PORT = 50051;
    private static final String ALAN = "Alan";
    private static final String VASCO_DA_GAMA = "Vasco da Gama";
    private static final String NA_BARREIRA = "Na barreira eu vou festejar e cantar outra vez com os loucos da saida três!";
    private GrpcServer blogServer;
    private BlogClient client;
    private ManagedChannel channel;
    private MongoClient mongoClient;

    @BeforeEach
    void init() throws IOException {
        initMongo();

        blogServer = new BlogServer(mongoClient);
        blogServer.init(PORT);
        blogServer.startServer();

        initChannel();
        client = new BlogClient(BlogServiceGrpc.newBlockingStub(channel), BlogServiceGrpc.newStub(channel));
    }

    private void initMongo() {
        mongoClient = MongoClients.create("mongodb://root:root@localhost:27017/");
        mongoClient.startSession();
    }

    private void initChannel() {
        channel = ManagedChannelBuilder.forAddress("localhost", PORT)
                .usePlaintext()
                .build();
    }

    @AfterEach
    void tearDown() {
        blogServer.stopServer();
        mongoClient.getDatabase("blogdb").getCollection("blog").drop();
        mongoClient.close();
    }

    @Test
    void shouldCreateBlogPostInDatabase() {
        String blogId = createABlogPost();
        assertThat(blogId).isNotBlank();
    }

    @Test
    void shouldFindBlogPostInDatabase() {
        String blogId = createABlogPost();

        Blog blogPost = client.readBlog(blogId);

        assertThat(blogPost.getAuthor()).isEqualTo(ALAN);
        assertThat(blogPost.getTitle()).isEqualTo(VASCO_DA_GAMA);
        assertThat(blogPost.getContent()).isEqualTo(NA_BARREIRA);
    }

    @Test
    void shouldRaiseExceptionWhenNoIdProvided() {
        assertThatThrownBy(() -> client.readBlog(""))
                .hasMessage("INVALID_ARGUMENT: The blog ID cannot be empty.")
                .isExactlyInstanceOf(StatusRuntimeException.class);
    }

    @Test
    void shouldRaiseExceptionWhenBlogPostNotFound() {
        assertThatThrownBy(() -> client.readBlog("512345325342145676432345"))
                .hasMessage("NOT_FOUND: Blog was not found.\n" +
                        "BlogId: 512345325342145676432345")
                .isExactlyInstanceOf(StatusRuntimeException.class);
    }

    @Test
    void shouldDeleteBlogPostFromDatabase() {
        String blogId = createABlogPost();

        client.deleteBlog(blogId);

        assertThatThrownBy(() -> client.readBlog(blogId))
                .hasMessage("NOT_FOUND: Blog was not found.\n" +
                        "BlogId: " + blogId)
                .isExactlyInstanceOf(StatusRuntimeException.class);
    }

    private String createABlogPost() {
        return client.createBlog(ALAN, VASCO_DA_GAMA, NA_BARREIRA);
    }

    @Test
    void shouldUpdateBlogPostOnDatabase() {
        String blogId = createABlogPost();

        client.updateBlog(blogId, "Alan Santo", VASCO_DA_GAMA, NA_BARREIRA);

        Blog updatedBlog = client.readBlog(blogId);
        assertThat(updatedBlog.getAuthor()).isEqualTo("Alan Santo");
    }

    @Test
    void shouldListAllBlogEntriesFromDatabase() {
        for (int i = 0; i < 3; i++) {
            createABlogPost();
        }

        List<Blog> blogs = client.listBlogs();

        assertThat(blogs.size()).isEqualTo(3);
    }
}