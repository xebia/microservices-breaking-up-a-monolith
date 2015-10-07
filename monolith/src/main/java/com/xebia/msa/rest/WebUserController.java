package com.xebia.msa.rest;

import com.xebia.msa.domain.WebUser;
import com.xebia.msa.repositories.WebUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/monolith/user")
public class WebUserController {

    private static Logger LOG = LoggerFactory.getLogger(WebUserController.class);

    @Autowired
    private WebUserRepository webUserRepository;

    private WebUserResourceAssembler webUserAssembler = new WebUserResourceAssembler();

    @RequestMapping(method = RequestMethod.POST, value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<WebUserResource> register(@RequestBody WebUserResource webUserResource) {
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
    public ResponseEntity<WebUserResource> allUserData(@PathVariable UUID uuid) {
        WebUser user = webUserRepository.findByUuid(uuid);
        if (user != null) {
            WebUserResource resource = webUserAssembler.toResource(user);
            return new ResponseEntity<WebUserResource>(resource, HttpStatus.OK);
        } else {
            return new ResponseEntity<WebUserResource>(HttpStatus.NOT_FOUND);
        }
    }
}
