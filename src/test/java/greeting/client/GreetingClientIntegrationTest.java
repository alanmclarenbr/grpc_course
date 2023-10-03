package greeting.client;

import calculator.server.CalculatorServer;
import com.proto.greeting.GreetingServiceGrpc;
import greeting.server.GreetingServer;
import grpc.GrpcServer;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

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
        client = new GreetingClient(GreetingServiceGrpc.newBlockingStub(channel));
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
}