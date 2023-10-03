package calculator.server;

import com.calculator.CalculatorServiceGrpc;
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
}
