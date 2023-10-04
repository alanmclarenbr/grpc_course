package calculator.client;

import com.calculator.CalculatorServiceGrpc;
import com.calculator.avg.AvgRequest;
import com.calculator.avg.AvgResponse;
import com.calculator.max.MaxRequest;
import com.calculator.max.MaxResponse;
import com.calculator.prime.PrimeRequest;
import com.calculator.prime.PrimeResponse;
import com.calculator.sum.SumRequest;
import com.calculator.sum.SumResponse;
import com.google.common.collect.Lists;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.concurrent.TimeUnit.SECONDS;

public class CalculatorClient {

    private CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorServiceBlockingStub;
    private CalculatorServiceGrpc.CalculatorServiceStub calculatorServiceStub;
    private Double result;

    public CalculatorClient(CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorServiceBlockingStub,
                            CalculatorServiceGrpc.CalculatorServiceStub calculatorServiceStub) {
        this.calculatorServiceBlockingStub = calculatorServiceBlockingStub;
        this.calculatorServiceStub = calculatorServiceStub;
    }

    public Integer sum(Integer firstDigit, Integer secondDigit) {
        SumRequest request = SumRequest.newBuilder()
                .setFirstDigit(firstDigit)
                .setSecondDigit(secondDigit)
                .build();
        SumResponse result = calculatorServiceBlockingStub.sum(request);
        return result != null ? result.getResult() : 0;
    }

    public List<Integer> calculatePrimes(Integer number) {
        PrimeRequest request = PrimeRequest.newBuilder()
                .setNumber(number)
                .build();

        ArrayList<PrimeResponse> primesResponses = newArrayList(calculatorServiceBlockingStub.primes(request));
        return primesResponses.stream()
                .map(PrimeResponse::getPrimeFactor)
                .collect(Collectors.toList());
    }

    public Double calculateAvg(List<Integer> elements) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        StreamObserver<AvgRequest> stream = calculatorServiceStub.avg(new StreamObserver<AvgResponse>() {
            @Override
            public void onNext(AvgResponse response) {
                result = response.getAvg();
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });

        elements.forEach(element -> stream.onNext(AvgRequest.newBuilder()
                .setNumber(element)
                .build()));

        stream.onCompleted();
        countDownLatch.await(3, SECONDS);

        return result;
    }

    public List<Integer> calculateMax(List<Integer> elements) throws InterruptedException {
        List<Integer> results = newArrayList();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        StreamObserver<MaxRequest> stream = calculatorServiceStub.max(new StreamObserver<MaxResponse>() {
            @Override
            public void onNext(MaxResponse response) {
                results.add(response.getMaximum());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });

        elements.forEach(element -> stream.onNext(MaxRequest.newBuilder().setNumber(element).build()));
        stream.onCompleted();

        countDownLatch.await(3, SECONDS);
        return results;
    }

}
