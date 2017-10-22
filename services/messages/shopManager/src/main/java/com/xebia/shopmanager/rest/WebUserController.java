package com.xebia.shopmanager.rest;

import com.xebia.shopmanager.domain.WebUser;
import com.xebia.shopmanager.repositories.WebUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shop/users")
public class WebUserController {

    private static final Logger LOG = LoggerFactory.getLogger(WebUserController.class);

    @Autowired
    private WebUserRepository webUserRepository;

    private WebUserResourceAssembler webUserAssembler = new WebUserResourceAssembler();

    @RequestMapping(method = RequestMethod.POST, value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<WebUserResource> register(@RequestBody WebUserResource webUserResource, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod() + ", CONTENT: "+webUserResource.toString());
        List<WebUser> users = webUserRepository.findByUsername(webUserResource.getUsername());
        if (users.size() > 0) {
            return new ResponseEntity<WebUserResource>(HttpStatus.FORBIDDEN);
        }
        WebUser newWebUser = new WebUser(UUID.randomUUID(), webUserResource.getUsername(), webUserResource.getPassword());
        WebUser webUser = webUserRepository.save(newWebUser);
        WebUserResource resource = webUserAssembler.toResource(webUser);
        return new ResponseEntity<WebUserResource>(resource, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{uuid}", produces = "application/json")
    public ResponseEntity<WebUserResource> allUserData(@PathVariable UUID uuid, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod());
        WebUser user = webUserRepository.findOne(uuid);
        if (user != null) {
            WebUserResource resource = webUserAssembler.toResource(user);
            return new ResponseEntity<WebUserResource>(resource, HttpStatus.OK);
        } else {
            return new ResponseEntity<WebUserResource>(HttpStatus.NOT_FOUND);
        }
    }
}
