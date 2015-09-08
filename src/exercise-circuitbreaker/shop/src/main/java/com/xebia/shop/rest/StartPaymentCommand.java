package com.xebia.shop.rest;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.xebia.shop.domain.Orderr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * Created by marco on 23/08/15.
 */
public class StartPaymentCommand extends HystrixCommand<PaymentResponse> {

    private static Logger LOG = LoggerFactory.getLogger(StartPaymentCommand.class);

    private final Orderr orderr;
    private boolean fail = false;

    public StartPaymentCommand(Orderr orderr, boolean fail) {

        // Excercise: set the execution timeout to < 1000 to make Hystrix call the Fallback service

        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Shop"))
                        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(5000))
                                //.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerRequestVolumeThreshold(50))
                                //.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerForceOpen(true))
                            // .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerForceClosed(true))
                        //.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerErrorThresholdPercentage(5))
                        //.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withMetricsRollingStatisticalWindowInMilliseconds(30000))
                        //.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(1))

        );
        this.orderr = orderr;
        this.fail = fail;
    }

    @Override
    protected PaymentResponse run() {
        // call payment service via REST call to provide info for payment interaction
        String paymentid = "";
        try {
            if (fail) {
                throw new RuntimeException("TEST EXCEPTION");
            }

            RestTemplate restTemplate = new RestTemplate();
            OrderrResource orderrResource = new OrderrResource(orderr.getUuid(), orderr.getTotal(), "");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            paymentid = restTemplate.postForObject("http://localhost:9001/payment", orderrResource, String.class);
            LOG.info("PAYMENT ID for Card from Payment Service: " + paymentid);

        }catch (RuntimeException re){
            LOG.error(re.getMessage());
            throw re;
        }
        return new PaymentResponse(UUID.fromString(paymentid), "CARD");

    }

    @Override
    protected PaymentResponse getFallback() {
        String altId =  "1av1e96r-g54d-rrad-b4j7-c23kh4abb959"; // this hardcoded id signifies an invoice id
        LOG.info("PAYMENT ID for Invoice from Alternative: " + altId);
        return new PaymentResponse(UUID.fromString("c9f05002-346d-4cd7-a5fc-92028502a4eb"), "INVOICE");
    }

    @Override
    protected String getCacheKey() {
        return orderr.getUuid().toString();
    }

}
