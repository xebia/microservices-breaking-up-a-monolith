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

    @RequestMapping("/clerk/list")
    @ResponseBody
    String listOfClerks() {
        logger.info("List method called!!");
        String json = "";

        List<Item> items = new ArrayList<Item>();
        items.add(new Item("1","clerk 1", "https://angularjs.org/", "Clerk 1 description"));
        items.add(new Item("2","clerk 2", "https://spring.io/", "Clerk 2 description"));
        json = itemsToJson(items);

        return json;
    }

    @RequestMapping("/fulfillment/list")
    @ResponseBody
    String listOfFulfillments() {
        logger.info("List method called!!");

        List<Item> items = new ArrayList<Item>();
        items.add(new Item("1","fulfillment 1", "https://angularjs.org/", "Fulfillment 1 description"));
        items.add(new Item("2","fulfillment 2", "https://spring.io/", "Fulfillment 2 description"));
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
