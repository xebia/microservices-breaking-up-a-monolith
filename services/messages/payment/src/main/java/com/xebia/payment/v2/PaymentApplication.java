package com.xebia.payment.v2;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@EnableSwagger
public class PaymentApplication {

    @Value("${rabbitmq.hostname}")
    private String hostname = "";

    @Value("${rabbitmq.port}")
    private String port = "";

    @Value("${rabbitmq.username}")
    private String username = "";

    @SuppressWarnings("all")
    @Value("${rabbitmq.password}")
    private String password = "";

    private SpringSwaggerConfig springSwaggerConfig;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(hostname, Integer.parseInt(port));
        connectionFactory.setConnectionTimeout(10);
        return connectionFactory;
    }

    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig;
    }

    @Bean
    public SwaggerSpringMvcPlugin customImplementation() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                .apiInfo(new ApiInfo(
                        "Payment API",
                        "This page provides details of the REST API for the Payment service",
                        "Go and explore ...",
                        null,
                        null,
                        null
                ))
                .useDefaultResponseMessages(false)
                .includePatterns("/.*");
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(PaymentApplication.class, args);
        applicationContext.getBean(PaymentApplication.class);
    }
}
