package com.xebia.fulfillment.events;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import com.xebia.shop.v2.rest.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.fulfillment.domain.Orderr;
import com.xebia.fulfillment.domain.Shipment;
import com.xebia.fulfillment.repositories.OrderRepository;
import com.xebia.fulfillment.repositories.ShipmentRepository;

@Component
public class EventListener {

    private static Logger LOG = LoggerFactory.getLogger(EventListener.class);
    private ObjectMapper mapper = new ObjectMapper();
	private CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ShipmentRepository shipmentRepository;

    @RabbitListener(queues = Config.handleFulfillment)
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
		        if(paymentReceived){
		        	Shipment shipment = this.shipmentRepository.findByOrderr(orderr);
		        	shipment.setStatus(Shipment.SHIPPABLE);
		        	this.shipmentRepository.save(shipment);
		        	LOG.info("Shipment updated to status:" + shipment.getStatus());
		        }
	        }
	        else{
	        	LOG.info("Could not find order with ID: " + orderID);
	        }
		} 
		catch (Exception e) {
			LOG.error("Error: " + e.getMessage());
		}
		latch.countDown();
    }

//	@RabbitListener(queues = "fulfillment.order")
    public void processOrderMessage(Object message) {
		LOG.info("Message is of type: " + message.getClass().getName());
		if(!(message instanceof byte[])) message = ((Message) message).getBody();
		String content = new String((byte[])message, StandardCharsets.UTF_8);
		
		LOG.info("Received on order: " + content);

		try {
	        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	        Orderr orderr = mapper.readValue(content, Orderr.class);
			this.orderRepository.save(orderr);
			String paymentStatus = Shipment.TO_BE_PAID;
			if(orderr.isPaymentReceived()) paymentStatus = Shipment.SHIPPABLE;
			Shipment shipment = new Shipment(UUID.randomUUID(), paymentStatus, orderr.getShippingAddress(), orderr);
			this.shipmentRepository.save(shipment);
			LOG.info("Order " +orderr.getUuid().toString()+ " received and created a shipment:\n" + shipment.toString());
		} 
		catch (Exception e) {
			LOG.error("Error: " + e.getMessage());
		}
		latch.countDown();
    }

}