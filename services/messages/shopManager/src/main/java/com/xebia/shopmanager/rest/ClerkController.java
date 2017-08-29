package com.xebia.shopmanager.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.shopmanager.Config;
import com.xebia.shopmanager.domain.Clerk;
import com.xebia.shopmanager.domain.ShopManager;
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

@Component
@RestController
@RequestMapping("/shop/session")
public class ClerkController {

    private static final Logger LOG = LoggerFactory.getLogger(ClerkController.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private WebUserRepository webUserRepository;

    @Autowired
    private ClerkRepository clerkRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    ShopManager shopManager;

    private ClerkResourceAssembler clerkResourceAssembler = new ClerkResourceAssembler();

    @RequestMapping(method = RequestMethod.POST, value = "/{userId}", produces = "application/json")
    public ResponseEntity<ClerkResource> createSession(@PathVariable UUID userId, HttpServletRequest request) {
        LOG.info("URL: " + request.getRequestURL() + ", METHOD: " + request.getMethod() + ", CONTENT: " + userId);
        WebUser user = webUserRepository.findOne(userId);
        if (user == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        try {
            Clerk clerk = new Clerk(user);
            clerkRepository.save(clerk);
            ClerkResource resource = clerkResourceAssembler.toResource(clerk);
            shopManager.registerClerk(clerk);
            String clerkAsJson = mapper.writeValueAsString(clerk);
            LOG.info("Sending START_SHOPPING event for clerk: \n" + clerkAsJson + "\n");
            rabbitTemplate.convertAndSend(Config.SHOP_EXCHANGE, Config.START_SHOPPING, clerkAsJson);
            return new ResponseEntity(resource, HttpStatus.CREATED);
        } catch (Exception e) {
            LOG.error("Error: " + e.getMessage());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{uuid}", produces = "application/json")
    public ResponseEntity<ClerkResource> clerk(@PathVariable UUID uuid, HttpServletRequest request) {
        LOG.info("URL: " + request.getRequestURL() + ", METHOD: " + request.getMethod());
        Clerk clerk = clerkRepository.findOne(uuid);
        if (clerk != null) {
            try {
                return new ResponseEntity(clerk.getDocument(), HttpStatus.OK);
            } catch (Exception e) {
                LOG.error("Error creating ResponseEntity " + e.getMessage());
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

}
