package calculator.server;

import com.calculator.CalculatorServiceGrpc;
import com.calculator.prime.PrimeRequest;
import com.calculator.prime.PrimeResponse;
import com.calculator.sum.SumRequest;
import com.calculator.sum.SumResponse;
import io.grpc.stub.StreamObserver;

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
}
