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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;

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
        client = new CalculatorClient(CalculatorServiceGrpc.newBlockingStub(channel), CalculatorServiceGrpc.newStub(channel));
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

    @Test
    void shouldCalculatePrimesForTwoHundredAndTen() {
        List<Integer> result = client.calculatePrimes(210);

        assertThat(result).containsExactlyElementsOf(list(2, 3, 5, 7));
    }

    @Test
    void shouldCalculatePrimesForHundredAndTen() {
        List<Integer> result = client.calculatePrimes(120);

        assertThat(result).containsExactlyElementsOf(list(2, 2, 2, 3, 5));
    }

    @Test
    void shouldCalculateAverage() throws InterruptedException {
        assertThat(client.calculateAvg(list(1, 2, 3, 4))).isEqualTo(2.5d);
    }

    @Test
    void shouldCalculateMaxElementEachRound() throws InterruptedException {
        assertThat(client.calculateMax(list(1, 5, 3, 6, 2, 20))).isEqualTo(list(1, 5, 5, 6, 6, 20));
    }

}