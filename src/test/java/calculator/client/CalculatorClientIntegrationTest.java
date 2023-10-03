package calculator.client;

import calculator.server.CalculatorServer;
import com.calculator.CalculatorServiceGrpc;
import grpc.GrpcServer;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class CalculatorClientIntegrationTest {

    private static final int PORT = 50051;
    private GrpcServer calculatorServer;
    private CalculatorClient client;
    private ManagedChannel channel;

    @BeforeEach
    void init() throws IOException {
        calculatorServer = new CalculatorServer();
        calculatorServer.init(PORT);
        calculatorServer.startServer();

        initChannel();
        client = new CalculatorClient(CalculatorServiceGrpc.newBlockingStub(channel));
    }

    private void initChannel() {
        channel = ManagedChannelBuilder.forAddress("localhost", PORT)
                .usePlaintext()
                .build();
    }

    @AfterEach
    void tearDown() {
        calculatorServer.stopServer();
    }

    @Test
    void shouldSumTenAndThreeAndReturnThirteen() {
        assertThat(client.sum(10, 3)).isEqualTo(13);
    }

    @Test
    void shouldSumMinusThreeAndSevenAndReturnFour() {
        assertThat(client.sum(-3, 7)).isEqualTo(4);
    }

}