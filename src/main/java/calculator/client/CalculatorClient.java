package calculator.client;

import com.calculator.CalculatorServiceGrpc;
import com.calculator.prime.PrimeRequest;
import com.calculator.prime.PrimeResponse;
import com.calculator.sum.SumRequest;
import com.calculator.sum.SumResponse;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CalculatorClient {

    private CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorService;

    public CalculatorClient(CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorService) {
        this.calculatorService = calculatorService;
    }

    public Integer sum(Integer firstDigit, Integer secondDigit) {
        SumRequest request = SumRequest.newBuilder()
                .setFirstDigit(firstDigit)
                .setSecondDigit(secondDigit)
                .build();
        SumResponse result = calculatorService.sum(request);
        return result != null ? result.getResult() : 0;
    }

    public List<Integer> calculatePrimes(Integer number) {
        PrimeRequest request = PrimeRequest.newBuilder()
                .setNumber(number)
                .build();

        ArrayList<PrimeResponse> primesResponses = Lists.newArrayList(calculatorService.primes(request));
        return primesResponses.stream()
                .map(PrimeResponse::getPrimeFactor)
                .collect(Collectors.toList());
    }

}
