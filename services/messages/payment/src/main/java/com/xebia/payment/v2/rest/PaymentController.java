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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

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
        Payment payment = null;
        try {
            payment = updateDocument(paymentId, cardNo);
            return new ResponseEntity(resourceAssembler.toResource(payment), HttpStatus.OK);
        } catch (Exception e) {
            LOG.info("payment: " + paymentId + " not found");
            return new ResponseEntity(paymentId, HttpStatus.NOT_FOUND);
        }
    }

    protected Payment updateDocument(UUID paymentId, String cardNo) throws Exception {
        Payment payment = paymentRepository.findOne(paymentId);
        if (payment == null) {
            LOG.info("payment: " + paymentId + " not found");
            throw new NoDataFoundException();
        }
        payment.setCardId(cardNo);
        payment.setDatePaid(new Date());
        payment = paymentRepository.save(payment);
        Clerk clerk = clerkRepository.findByPayment(payment);
        LOG.info("Sending orderPaid event, new document: \n" + clerk.getDocument() + "\n");
        rabbitTemplate.convertAndSend(Config.shopExchange, Config.orderPaid, clerk.getDocument());
        return payment;
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
        LOG.info("URL: " + request.getRequestURL() + ", METHOD: " + request.getMethod() + ", CONTENT: clerkId=" + clerkId.toString());
        Clerk clerk = clerkRepository.findOne(clerkId);
        Payment payment = clerk.getPayment();
        PaymentResource resource = new PaymentResourceAssembler().toResource(payment);
        return new ResponseEntity(resource, HttpStatus.OK);
    }
}
