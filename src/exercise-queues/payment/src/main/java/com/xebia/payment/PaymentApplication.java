package com.xebia.payment;

import com.xebia.payment.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class PaymentApplication {

    private static Logger LOG = LoggerFactory.getLogger(PaymentApplication.class);

    @Autowired
    private PaymentRepository paymentRepository;

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(PaymentApplication.class, args);
        applicationContext.getBean(PaymentApplication.class);
    }
}
