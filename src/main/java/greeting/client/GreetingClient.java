package greeting.client;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

public class GreetingClient {

    private GreetingServiceGrpc.GreetingServiceBlockingStub greetingService;

    public GreetingClient(GreetingServiceGrpc.GreetingServiceBlockingStub greetingService) {
        this.greetingService = greetingService;
    }

    public String doGreet(String name) {
        GreetingResponse response = greetingService.greet(GreetingRequest.newBuilder().setFirstName(name).build());

        return response.getResult();
    }

    public List<String> doGreetManyTimes(String name) {
        List<GreetingResponse> greetingResponses = newArrayList(greetingService.greetManyTimes(GreetingRequest.newBuilder()
                .setFirstName(name)
                .build()));

        return greetingResponses.stream()
                .map(GreetingResponse::getResult)
                .collect(toList());
    }
}
