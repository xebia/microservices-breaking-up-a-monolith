package com.xebia.shopmanager;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@RequestMapping("/user")
public class UserResource {

    @RequestMapping(method = RequestMethod.GET, produces = "text/html")
    public String getIndex() {
        return "User is not logged in";
    }
}
