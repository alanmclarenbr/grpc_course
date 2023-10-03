package primes.api.client;

import com.google.common.collect.Lists;
import com.primes.api.PrimesRequest;
import com.primes.api.PrimesResponse;
import com.primes.api.PrimesServiceGrpc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PrimesClient {

    private PrimesServiceGrpc.PrimesServiceBlockingStub primesService;

    public PrimesClient(PrimesServiceGrpc.PrimesServiceBlockingStub primesService) {
        this.primesService = primesService;
    }

    public List<Integer> calculatePrimes(Integer number) {
        PrimesRequest request = PrimesRequest.newBuilder()
                .setNumber(number)
                .build();

        ArrayList<PrimesResponse> primesResponses = Lists.newArrayList(primesService.calculatePrimes(request));
        return primesResponses.stream()
                .map(PrimesResponse::getResult)
                .collect(Collectors.toList());
    }
}
