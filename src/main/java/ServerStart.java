import calculator.server.CalculatorServer;

import java.io.IOException;

public class ServerStart {

    public static void main(String[] args) throws IOException, InterruptedException {
        CalculatorServer server = new CalculatorServer();

        server.init(50051);
        server.startServer();

        Runtime.getRuntime().addShutdownHook(new Thread(server::stopServer));
        server.awaitTermination();
    }
}
