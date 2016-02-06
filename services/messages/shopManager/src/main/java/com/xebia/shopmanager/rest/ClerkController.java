package com.xebia.shopmanager.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.shopmanager.Config;
import com.xebia.shopmanager.domain.Clerk;
import com.xebia.shopmanager.domain.WebUser;
import com.xebia.shopmanager.repositories.ClerkRepository;
import com.xebia.shopmanager.repositories.WebUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Component
@RestController
@RequestMapping("/shop/session")
public class ClerkController {

    private static Logger LOG = LoggerFactory.getLogger(ClerkController.class);

    private ObjectMapper mapper = new ObjectMapper();
    private CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    private WebUserRepository webUserRepository;

    @Autowired
    private ClerkRepository clerkRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    private ClerkResourceAssembler clerkResourceAssembler = new ClerkResourceAssembler();

    @RequestMapping(method = RequestMethod.POST, value = "/{userId}", produces = "application/json")
    public ResponseEntity<ClerkResource> createSession(@PathVariable UUID userId, HttpServletRequest request) {
        LOG.info("URL: " + request.getRequestURL() + ", METHOD: " + request.getMethod() + ", CONTENT: " + userId);
        WebUser user = webUserRepository.findOne(userId);
        if (user == null) {
            return new ResponseEntity<ClerkResource>(HttpStatus.NOT_FOUND);
        }
        Clerk clerk = new Clerk(user);
        clerkRepository.save(clerk);
        ClerkResource resource = clerkResourceAssembler.toResource(clerk);

        try {
            String clerkAsJson = mapper.writeValueAsString(clerk);
            LOG.info("Sending startShopping event for clerk: \n" + clerkAsJson + "\n");
            rabbitTemplate.convertAndSend(Config.shopExchange, Config.startShoppingRoutingKey, clerkAsJson);
            return new ResponseEntity<ClerkResource>(resource, HttpStatus.CREATED);
        } catch (Exception e) {
            LOG.error("Error: " + e.getMessage());
            return new ResponseEntity<ClerkResource>(resource, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{uuid}", produces = "application/json")
    public ResponseEntity<ClerkResource> clerk(@PathVariable UUID uuid, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod());
        Clerk clerk = clerkRepository.findOne(uuid);
        if (clerk != null) {
            ClerkResource resource = clerkResourceAssembler.toResource(clerk);
            try {
                String data = mapper.writeValueAsString(clerk);
                LOG.info("Clerk in get by uuid: " + data);
                LOG.info("resource: " + resource);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new ResponseEntity<ClerkResource>(resource, HttpStatus.OK);
        } else {
            return new ResponseEntity<ClerkResource>(HttpStatus.NOT_FOUND);
        }
    }

}
