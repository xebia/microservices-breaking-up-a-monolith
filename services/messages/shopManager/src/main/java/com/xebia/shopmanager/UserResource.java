package com.xebia.shopmanager;

import com.xebia.shopmanager.rest.WebUserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@RequestMapping("/user")
public class UserResource {

    @Autowired
    WebUserController webUserController;

    boolean loggedIn = false;

    @RequestMapping(method = RequestMethod.GET, produces = "text/html")
    public String getIndex() {
        if(loggedIn) {
            return "you are logged in!";
        } else {
            return " <form action=\"/user\">\n" +
                    "  username:<br>\n" +
                    "  <input type=\"text\" name=\"username\"><br>\n" +
                    "  password:<br>\n" +
                    "  <input type=\"text\" name=\"password\">\n" +
                    "<input type=\"submit\" value=\"Log in\">" +
                    "</form> ";
        }
    }


    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String login() {
        webUserController.createWebUser("bla", "diebla");
        loggedIn = true;
        return "<div>" +
                "logged in" +
                "</div>"
                ;
    }
}
