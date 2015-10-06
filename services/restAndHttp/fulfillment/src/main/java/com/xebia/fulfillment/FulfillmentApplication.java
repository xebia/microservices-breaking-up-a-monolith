package com.xebia.fulfillment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class FulfillmentApplication {

    private static Logger LOG = LoggerFactory.getLogger(FulfillmentApplication.class);


    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(FulfillmentApplication.class, args);
        FulfillmentApplication application = applicationContext.getBean(FulfillmentApplication.class);
    }

    @Bean
    public FilterRegistrationBean commonsRequestLoggingFilter()
    {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new CommonsRequestLoggingFilter());
        return registrationBean;
    }

}

