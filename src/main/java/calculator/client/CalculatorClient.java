package calculator.client;

import com.calculator.CalculatorServiceGrpc;
import com.calculator.sum.SumRequest;
import com.calculator.sum.SumResponse;

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

}
