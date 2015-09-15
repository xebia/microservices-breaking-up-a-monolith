package com.xebia.payment.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.payment.domain.*;
import com.xebia.payment.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    final static String shopExchange = "shop";
    final static String paymentRoutingKey = "payments";
    private ObjectMapper mapper = new ObjectMapper();
    
    private static Logger LOG = LoggerFactory.getLogger(PaymentController.class);

    private PaymentResourceAssembler paymentResourceAssembler = new PaymentResourceAssembler();

	@Autowired
	RabbitTemplate rabbitTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    @RequestMapping(method =RequestMethod.POST, consumes = "application/json", produces = "text/plain")
    public ResponseEntity<String> startnewPaymentProcess(@RequestBody OrderrResource orderrResource, HttpServletRequest request) {
        LOG.info("Payment service received request to start payment process for order: " + orderrResource.getUuid());
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: "+orderrResource.toString());
        try {
            Thread.sleep(500);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        Payment payment = paymentRepository.save(new Payment(orderrResource));
        return new ResponseEntity<String>(payment.getUuid().toString(), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/pay/{orderId}/creditcard/{cardNo}", produces = "application/json")
    public ResponseEntity<PaymentResource> pay(@PathVariable UUID orderId, @PathVariable String cardNo, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: orderId="+orderId+", cardNo="+cardNo);
        Payment payment = paymentRepository.findByOrderUuid(orderId);
        if (payment == null) {
            LOG.info("payment for order: " + orderId + " not found");
            return new ResponseEntity(orderId, HttpStatus.NOT_FOUND);
        }
        payment.setCardId(cardNo);
        payment.setDatePaid(new Date());
        paymentRepository.save(payment);

        // Exercise
        // This used to be a synchronous REST call informing the shop that a payment was received
        // We've changed this into an event so both Order and Fulfillment can do their job
        HashMap<String,Object> paymentPayload = new HashMap<String,Object>();
        paymentPayload.put("orderUUID", payment.getOrderUuid().toString());
        paymentPayload.put("paymentReceived", true);
        try {
            LOG.info("Sending payment received for " + payment);
			rabbitTemplate.convertAndSend(shopExchange, paymentRoutingKey, mapper.writeValueAsString(paymentPayload));
		} catch (Exception e) {
            LOG.error("Error: " + e.getMessage());
        }
        
        PaymentResource resource = new PaymentResourceAssembler().toResource(payment);
        LOG.info("End method: pay");
        return new ResponseEntity<PaymentResource>(resource, HttpStatus.OK);
    }

    private static final DateFormat dataFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

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
