package com.xebia.frontendfortest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan("com.xebia.frontendfortest")
public class FulfillmentWebAppInitializer {

    public static void main(String[] args) throws Exception{
        SpringApplication.run(FulfillmentWebAppInitializer.class, args);
    }
}

