package com.xebia.shop.events;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.shop.domain.Orderr;
import com.xebia.shop.repositories.OrderRepository;


@Component
public class EventListener {

    private static Logger LOG = LoggerFactory.getLogger(EventListener.class);
    private ObjectMapper mapper = new ObjectMapper();
	private CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    private OrderRepository orderRepository;

	// Exercise 7
	// This is the OrderPaid event, see diagram
    @RabbitListener(queues = "shop.payment")
    public void processPaymentMessage(Object message) {
		LOG.info("Message is of type: " + message.getClass().getName());
		if(!(message instanceof byte[])) message = ((Message) message).getBody();
		String content = new String((byte[])message, StandardCharsets.UTF_8);
    	LOG.info("Received on payment: " + content);
		try {
	        HashMap payment = mapper.readValue(content, HashMap.class);
			LOG.info("Payment [" +payment.toString()+ "] received.");
	        String orderID = payment.get("orderUUID").toString();
	        boolean paymentReceived = ((Boolean) payment.get("paymentReceived")).booleanValue();
	        Orderr orderr = this.orderRepository.findOne(UUID.fromString(orderID));
	        if(orderr != null){
		        orderr.setPaymentReceived(paymentReceived);
		        this.orderRepository.save(orderr);
	        }
	        else{
	        	LOG.info("Could not find order with ID: " + orderID);
	        }
		} 
		catch (Exception e) {
			LOG.error("processPaymentMessage Error: " + e.getMessage());
		}
		latch.countDown();
    }

}