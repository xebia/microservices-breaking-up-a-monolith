package com.xebia.payment.rest;

import com.xebia.payment.domain.*;
import com.xebia.payment.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private static Logger LOG = LoggerFactory.getLogger(PaymentController.class);

    private PaymentResourceAssembler paymentResourceAssembler = new PaymentResourceAssembler();

    @Autowired
    private PaymentRepository paymentRepository;

    @RequestMapping(method =RequestMethod.POST, consumes = "application/json", produces = "text/plain")
    public ResponseEntity<String> startnewPaymentProcess(@RequestBody OrderrResource orderrResource, HttpServletRequest request) {

        // Excercise: you can play with sleep() to make Hystrix trigger a call to the Fallback service.

        try {
            Thread.sleep(1000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: "+orderrResource.toString());
        LOG.info("Payment service received request to start payment process for order: " + orderrResource.getUuid());

        Payment payment = paymentRepository.save(new Payment(orderrResource));
        return new ResponseEntity<String>(payment.getUuid().toString(), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/pay/{id}/creditcard/{cardNo}", produces = "application/json")
    public ResponseEntity<PaymentResource> pay(@PathVariable UUID id, @PathVariable String cardNo, @RequestHeader String registerurl, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: paymentId="+id+", cardNo="+cardNo);
        Payment payment = paymentRepository.findOne(id);
        if (payment == null) {
            return new ResponseEntity(id, HttpStatus.NOT_FOUND);
        }
        payment.setCardId(cardNo);
        payment.setDatePaid(new Date());
        paymentRepository.save(payment);

        if (!registerurl.isEmpty() && (registerurl.startsWith("http") || registerurl.startsWith("HTTP"))) {
            //register payment with shop service
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> params = new HashMap<String, String>();
            params.put("id", payment.getOrderUuid().toString());
            restTemplate.put(registerurl, String.class, params);     //"http://localhost:9002/cart/orders/registerPayment/{id}"
        }
        PaymentResource resource = new PaymentResourceAssembler().toResource(payment);
        LOG.info("End method: pay");

        return new ResponseEntity<PaymentResource>(resource, HttpStatus.OK);
    }

    private static final DateFormat dataFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

    @RequestMapping(method = RequestMethod.GET, value ="/list", produces = "application/json", headers = {"If-Modified-Since" })
    public ResponseEntity<PaymentResource> getListOfPayments(
            @RequestHeader("If-Modified-Since")
            @DateTimeFormat(pattern = "EEE, dd MMM yyyy HH:mm:ss z") final Date ifModifiedSince
            , HttpServletRequest request
    ) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod());
        List<Payment> payments = paymentRepository.findByDatePaidGreaterThan(ifModifiedSince);
        List<PaymentResource> paymentResources = new PaymentResourceAssembler().toResource(payments);
        return new ResponseEntity(paymentResources, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value ="/all", produces = "application/json")
    public ResponseEntity<PaymentResource> getAllPayments(HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod());
        Iterable<Payment> payments = paymentRepository.findAll();
        List<Payment> result = new ArrayList<Payment>();
        for(Payment payment: payments) {
            result.add(payment);
        }
        List<PaymentResource> paymentResources = new PaymentResourceAssembler().toResource(result);
        return new ResponseEntity(paymentResources, HttpStatus.OK);
    }

}
