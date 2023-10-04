package calculator.server;

import com.calculator.CalculatorServiceGrpc;
import com.calculator.avg.AvgRequest;
import com.calculator.avg.AvgResponse;
import com.calculator.max.MaxRequest;
import com.calculator.max.MaxResponse;
import com.calculator.prime.PrimeRequest;
import com.calculator.prime.PrimeResponse;
import com.calculator.sum.SumRequest;
import com.calculator.sum.SumResponse;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class CalculatorServerImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        responseObserver.onNext(SumResponse.newBuilder()
                .setResult(request.getFirstDigit() + request.getSecondDigit())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void primes(PrimeRequest request, StreamObserver<PrimeResponse> responseObserver) {
        int number = request.getNumber();
        int divisor = 2;
        while (number > 1) {
            if (number % divisor == 0) {
                responseObserver.onNext(PrimeResponse.newBuilder()
                        .setPrimeFactor(divisor)
                        .build());

                number /= divisor;
            } else {
                divisor++;
            }
        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<AvgRequest> avg(StreamObserver<AvgResponse> responseObserver) {
        List<Integer> elements = newArrayList();

        return new StreamObserver<AvgRequest>() {
            @Override
            public void onNext(AvgRequest request) {
                elements.add(request.getNumber());
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                Integer elementsSum = elements.stream()
                        .reduce(Integer::sum).orElse(0);
                int divisor = elements.size() != 0 ? elements.size() : 1;


                responseObserver.onNext(AvgResponse.newBuilder()
                        .setAvg(elementsSum.doubleValue() / (double) divisor)
                        .build());

                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<MaxRequest> max(StreamObserver<MaxResponse> responseObserver) {
        return new StreamObserver<MaxRequest>() {
            List<Integer> elements = newArrayList();
            @Override
            public void onNext(MaxRequest request) {
                elements.add(request.getNumber());
                int maximum = elements.stream()
                        .max(Integer::compareTo)
                        .orElse(0);
                responseObserver.onNext(MaxResponse.newBuilder().setMaximum(maximum).build());
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
