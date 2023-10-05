package greeting.client;

import com.proto.greeting.GreetingServiceGrpc;
import greeting.server.GreetingServer;
import grpc.GrpcServer;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Lists.list;

class GreetingClientIntegrationTest {

    private static final int PORT = 50051;
    private GrpcServer greetingServer;
    private GreetingClient client;
    private ManagedChannel channel;

    @BeforeEach
    void init() throws IOException {
        greetingServer = new GreetingServer();
        greetingServer.init(PORT);
        greetingServer.startServer();

        initChannel();
        client = new GreetingClient(GreetingServiceGrpc.newBlockingStub(channel), GreetingServiceGrpc.newStub(channel));
    }

    private void initChannel() {
        channel = ManagedChannelBuilder.forAddress("localhost", PORT)
                .usePlaintext()
                .build();
    }

    @AfterEach
    void tearDown() {
        greetingServer.stopServer();
    }

    @Test
    void shouldGreetOnce() {
        assertThat(client.doGreet("Alan")).isEqualTo("Hello Alan");
    }

    @Test
    void shouldGreetTenTimes() {
        assertThat(client.doGreetManyTimes("Alan")).containsOnly("Hello Alan").hasSize(10);
    }

    @Test
    void shouldLongGreet() throws InterruptedException {
        assertThat(client.longGreet(list("Alan", "Elis", "Ronaldinho"))).isEqualTo("Hello Alan!\nHello Elis!\nHello Ronaldinho!\n");
    }

    @Test
    void shouldGreetEveryone() throws InterruptedException {
        assertThat(client.greetEveryone(list("Alan", "Elis", "Ronaldinho"))).containsExactlyElementsOf(list("Hello Alan", "Hello Elis", "Hello Ronaldinho"));
    }

    @Test
    void shouldThrowExceptionForExceedingDeadline() {
        assertThatThrownBy(() -> client.greetWithDeadline("Alan"))
                .hasMessageContaining("DEADLINE_EXCEEDED: deadline exceeded after")
                .isExactlyInstanceOf(StatusRuntimeException.class);
    }
}