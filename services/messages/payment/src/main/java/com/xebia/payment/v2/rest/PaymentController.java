package com.xebia.payment.v2.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.payment.v2.Config;
import com.xebia.payment.v2.domain.Clerk;
import com.xebia.payment.v2.domain.Payment;
import com.xebia.payment.v2.repositories.ClerkRepository;
import com.xebia.payment.v2.repositories.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/payment/v2")
public class PaymentController {
    private ObjectMapper mapper = new ObjectMapper();

    private static Logger LOG = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    ClerkRepository clerkRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private PaymentResourceAssembler resourceAssembler = new PaymentResourceAssembler();

    @RequestMapping(method = RequestMethod.PUT, value = "/pay/{paymentId}/creditcard/{cardNo}", produces = "application/json")
    public ResponseEntity<PaymentResource> pay(@PathVariable UUID paymentId, @PathVariable String cardNo, HttpServletRequest request) {
        LOG.info("URL: " + request.getRequestURL() + ", METHOD: " + request.getMethod() + ", CONTENT: paymentId=" + paymentId + ", cardNo=" + cardNo);
        Payment payment = paymentRepository.findOne(paymentId);
        if (payment == null) {
            LOG.info("payment: " + paymentId + " not found");
            return new ResponseEntity(paymentId, HttpStatus.NOT_FOUND);
        }
        payment.setCardId(cardNo);
        payment.setDatePaid(new Date());
        payment = paymentRepository.save(payment);
        Clerk clerk = clerkRepository.findByPayment(payment);
        try {
            String clerkAsJson = mapper.writeValueAsString(clerk);
            LOG.info("Sending orderPaid event for clerk: \n" + clerkAsJson + "\n");
            rabbitTemplate.convertAndSend(Config.shopExchange, Config.orderPaid, clerkAsJson);
            PaymentResource resource = resourceAssembler.toResource(payment);
            return new ResponseEntity(resource, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error: " + e.getMessage());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{paymentId}", produces = "application/json")
    public ResponseEntity<PaymentResource> getOne(@PathVariable UUID paymentId, HttpServletRequest request) {
        LOG.info("URL: " + request.getRequestURL() + ", METHOD: " + request.getMethod());
        Payment payment = paymentRepository.findOne(paymentId);
        PaymentResource paymentResource = new PaymentResourceAssembler().toResource(payment);
        return new ResponseEntity(paymentResource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/forClerk/{clerkId}", produces = "application/json")
    public ResponseEntity<PaymentResource> findPaymentByClerk(@PathVariable UUID clerkId, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: clerkId="+clerkId.toString());
        Clerk clerk = clerkRepository.findOne(clerkId);
        Payment payment = clerk.getPayment();
        PaymentResource resource = new PaymentResourceAssembler().toResource(payment);
        return new ResponseEntity<PaymentResource>(resource, HttpStatus.OK);
    }
}
