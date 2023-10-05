package greeting.client;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.Deadline;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

public class GreetingClient {

    private GreetingServiceGrpc.GreetingServiceBlockingStub greetingServiceBlockingStub;
    private GreetingServiceGrpc.GreetingServiceStub greetingServiceStub;
    private String result;
    private List<String> greetings = newArrayList();

    public GreetingClient(GreetingServiceGrpc.GreetingServiceBlockingStub greetingServiceBlockingStub,
                          GreetingServiceGrpc.GreetingServiceStub greetingServiceStub) {
        this.greetingServiceBlockingStub = greetingServiceBlockingStub;
        this.greetingServiceStub = greetingServiceStub;
    }

    public String doGreet(String name) {
        GreetingResponse response = greetingServiceBlockingStub.greet(GreetingRequest.newBuilder().setFirstName(name).build());

        return response.getResult();
    }

    public List<String> doGreetManyTimes(String name) {
        List<GreetingResponse> greetingResponses = newArrayList(greetingServiceBlockingStub.greetManyTimes(GreetingRequest.newBuilder()
                .setFirstName(name)
                .build()));

        return greetingResponses.stream()
                .map(GreetingResponse::getResult)
                .collect(toList());
    }

    public String longGreet(List<String> names) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        StreamObserver<GreetingRequest> stream = greetingServiceStub.longGreet(new StreamObserver<GreetingResponse>() {
            @Override
            public void onNext(GreetingResponse response) {
                result = response.getResult();
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });

        names.forEach(name -> stream.onNext(GreetingRequest.newBuilder().setFirstName(name).build()));
        stream.onCompleted();
        countDownLatch.await(3, SECONDS);

        return result;
    }

    public List<String> greetEveryone(List<String> names) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        StreamObserver<GreetingRequest> stream = greetingServiceStub.greetEveryone(new StreamObserver<GreetingResponse>() {
            @Override
            public void onNext(GreetingResponse response) {
                greetings.add(response.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });

        names.forEach(name -> stream.onNext(GreetingRequest.newBuilder().setFirstName(name).build()));
        stream.onCompleted();
        countDownLatch.await(3, SECONDS);

        return greetings;
    }

    public String greetWithDeadline(String name) {
        GreetingResponse greetingResponse = greetingServiceBlockingStub
                .withDeadline(Deadline.after(100, MILLISECONDS))
                .greetWithDeadline(GreetingRequest.newBuilder()
                        .setFirstName(name)
                        .build());

        return greetingResponse.getResult();
    }
}
