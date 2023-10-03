package primes.api.client;

import com.primes.api.PrimesServiceGrpc;
import grpc.GrpcServer;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import primes.api.server.PrimesServer;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;

class PrimesClientIntegrationTest {

    private static final int PORT = 50051;
    private GrpcServer primesServer;
    private PrimesClient client;
    private ManagedChannel channel;

    @BeforeEach
    void init() throws IOException {
        primesServer = new PrimesServer();
        primesServer.init(PORT);
        primesServer.startServer();

        initChannel();
        client = new PrimesClient(PrimesServiceGrpc.newBlockingStub(channel));
    }

    private void initChannel() {
        channel = ManagedChannelBuilder.forAddress("localhost", PORT)
                .usePlaintext()
                .build();
    }

    @AfterEach
    void tearDown() {
        primesServer.stopServer();
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
}