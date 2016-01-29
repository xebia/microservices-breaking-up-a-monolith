package com.xebia.shopmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class ShopManagerApplication {

    @Value("${rabbitmq.hostname}")
    private String hostname="";

    @Value("${rabbitmq.port}")
    private String port="";

    @Value("${rabbitmq.username}")
    private String username="";

    @Value("${rabbitmq.password}")
    private String password="";

    private static Logger LOG = LoggerFactory.getLogger(ShopManagerApplication.class);

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(hostname, Integer.parseInt(port));
        connectionFactory.setConnectionTimeout(10);
        return connectionFactory;
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ShopManagerApplication.class, args);
        ShopManagerApplication application = applicationContext.getBean(ShopManagerApplication.class);
    }
}
