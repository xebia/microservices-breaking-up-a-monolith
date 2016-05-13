package com.xebia.shop.v2;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@RequestMapping("/index.html")
public class IndexResource {

    @RequestMapping(method = RequestMethod.GET, produces = "text/html")
    public String getIndex() {
        return "Hello I'm the shop service";
    }
}
