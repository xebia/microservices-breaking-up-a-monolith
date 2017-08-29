package com.xebia.shop.v2;

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
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableSwagger
public class ShopApplication {

    @Value("${rabbitmq.hostname}")
    private String hostname="";

    @Value("${rabbitmq.port}")
    private String port="";

    @Value("${rabbitmq.username}")
    private String username="";

    @SuppressWarnings("all")
    @Value("${rabbitmq.password}")
    private String password="";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(hostname, Integer.parseInt(port));
        connectionFactory.setConnectionTimeout(10);
        return connectionFactory;
    }

    private SpringSwaggerConfig springSwaggerConfig;

    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig;
    }

    @Bean
    public SwaggerSpringMvcPlugin customImplementation() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                .apiInfo(new ApiInfo(
                        "Shop API",
                        "This page provides details of the REST API for the Shop service",
                        "Go and explore ...",
                        null,
                        null,
                        null
                ))
                .useDefaultResponseMessages(false)
                .includePatterns("/.*")
                .genericModelSubstitutes(ResponseEntity.class);
    }

    @Bean
    public FilterRegistrationBean commonsRequestLoggingFilter()
    {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new CommonsRequestLoggingFilter());
        return registrationBean;
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ShopApplication.class, args);
        applicationContext.getBean(ShopApplication.class);
    }

}
