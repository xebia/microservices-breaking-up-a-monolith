package com.xebia.fulfillment.v2.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.fulfillment.v2.Config;
import com.xebia.fulfillment.v2.domain.Clerk;
import com.xebia.fulfillment.v2.domain.Document;
import com.xebia.fulfillment.v2.domain.Shipment;
import com.xebia.fulfillment.v2.repositories.ClerkRepository;
import com.xebia.fulfillment.v2.repositories.DocumentRepository;
import com.xebia.fulfillment.v2.repositories.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Component
public class EventListener {

    private static Logger LOG = LoggerFactory.getLogger(EventListener.class);
    private ObjectMapper mapper = new ObjectMapper();
	private CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    private ShipmentRepository shipmentRepository;

	@Autowired
	ClerkRepository clerkRepository;

	@Autowired
	DocumentRepository documentRepository;

	@RabbitListener(queues = Config.handleFulfillment)
	public void processFulfillmentMessage(Object message) {
		if(!(message instanceof byte[])) message = ((Message) message).getBody();
		String content = new String((byte[])message, StandardCharsets.UTF_8);
		LOG.info("Received new order to be paid: " + content);
		try {
			createShipment(content);
		} catch (Exception e) {
			LOG.error("Error: " + e.getMessage());
		}
		latch.countDown();
	}

	public Shipment createShipment(Clerk clerk) throws IOException {
		Shipment shipment = new Shipment(UUID.randomUUID());
		shipmentRepository.save(shipment);
		clerk.setShipment(shipment);
		clerkRepository.save(clerk);
		LOG.info("Created shipment for clerk: " + mapper.writeValueAsString(clerk));
		return shipment;
	}

	public Shipment createShipment(Document document) throws IOException {
		if (document.getUuid() == null) {
			document.setUuid(UUID.randomUUID());
		}
		documentRepository.save(document);
		Clerk clerk = document.getClerk();
		return createShipment(clerk);
	}

	public Shipment createShipment(String content) throws IOException {
		Document document = new Document(content);
		return createShipment(document);
	}
}
