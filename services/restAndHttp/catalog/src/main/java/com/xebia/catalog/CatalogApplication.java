package com.xebia.catalog;

import com.xebia.catalog.repositories.ProductRepository;
import com.xebia.catalog.domain.Product;

import java.util.UUID;

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
public class CatalogApplication {

    private static Logger LOG = LoggerFactory.getLogger(CatalogApplication.class);

    @Autowired
    private ProductRepository productRepository;

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(CatalogApplication.class, args);
        CatalogApplication application = applicationContext.getBean(CatalogApplication.class);
        
        Product product = new Product(UUID.randomUUID(),"product 1","supplier 1", new Double(112.10));
        product = application.productRepository.save(product);

        Product product2 = new Product(UUID.fromString("65c4cffb-1bb3-4742-bd21-c68ca01a818c"),"product 2","supplier 1", new Double(34.10));
        product2 = application.productRepository.save(product2);
        
    }
}
