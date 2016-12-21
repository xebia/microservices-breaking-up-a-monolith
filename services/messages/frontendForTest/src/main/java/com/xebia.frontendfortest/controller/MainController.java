package com.xebia.frontendfortest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class MainController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value="/",method = RequestMethod.GET)
    public String homepage(){
        return "index.html";
    }

    @RequestMapping("/fulfillment/list")
    @CrossOrigin(origins = "http://localhost:8082", maxAge = 3600,
       allowedHeaders={"x-requested-with"})
    @ResponseBody
    String listOfFulfillments() {
        logger.info(">>>> 8083 - Fulfillment - List method");
        Date date = new Date();
        List<Item> items = new ArrayList<Item>();
        items.add(new Item("1","fulfillment 1", "https://angularjs.org/", "Fulfillment 1 " + date));
        items.add(new Item("2","fulfillment 2", "https://spring.io/", "Fulfillment 2 " + date));
        return itemsToJson(items);
    }


    private String itemsToJson(List<Item> items) {
        String json = "[]";
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
