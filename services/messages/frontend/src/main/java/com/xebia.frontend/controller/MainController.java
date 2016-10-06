package com.xebia.frontend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value="/",method = RequestMethod.GET)
    public String homepage(){
        return "index.html";
    }

    @RequestMapping("/list")
    @ResponseBody
    String standardResponse() {
        logger.info("List method called!!");
        String json = "";

        List<Item> items = new ArrayList<Item>();
        items.add(new Item("1","Angular", "https://angularjs.org/", "Angular description"));
        items.add(new Item("2","Spring", "https://spring.io/", "Spring description"));
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            json = objectMapper.writeValueAsString(items);
            logger.info(json);
        }
        catch (JsonProcessingException ex){
            logger.error(ex.getMessage());
        }

        return json;
    }

}
