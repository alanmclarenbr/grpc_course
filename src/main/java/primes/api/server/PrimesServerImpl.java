package primes.api.server;

import com.primes.api.PrimesRequest;
import com.primes.api.PrimesResponse;
import com.primes.api.PrimesServiceGrpc;
import io.grpc.stub.StreamObserver;

public class PrimesServerImpl extends PrimesServiceGrpc.PrimesServiceImplBase {

    @Override
    public void calculatePrimes(PrimesRequest request, StreamObserver<PrimesResponse> responseObserver) {
        int number = request.getNumber();
        int k = 2;
        while (number > 1) {
            if (number % k == 0) {
                responseObserver.onNext(PrimesResponse.newBuilder().setResult(k).build());
                number /= k;
            } else {
                k++;
            }
        }

        responseObserver.onCompleted();
    }
}
