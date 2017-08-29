package com.xebia.catalog;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import com.xebia.catalog.domain.Product;
import com.xebia.catalog.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableSwagger
public class CatalogApplication {

    @Autowired
    private ProductRepository productRepository;

    private SpringSwaggerConfig springSwaggerConfig;

    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig;
    }

    @Bean
    public SwaggerSpringMvcPlugin customImplementation() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
            //Root level documentation
            .apiInfo(new ApiInfo(
                "Catalog API",
                "This page provides details of the REST API for the Catalog service",
                "Go and explore ...",
                null,
                null,
                null
            ))
            .useDefaultResponseMessages(false)
                //Map the specific URL patterns into Swagger
            .includePatterns("/.*");
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(CatalogApplication.class, args);
        CatalogApplication application = applicationContext.getBean(CatalogApplication.class);
        
        Product product = new Product(UUID.randomUUID(),"product 1","supplier 1", Double.valueOf(112.10));
        application.productRepository.save(product);

        Product product2 = new Product(UUID.fromString("65c4cffb-1bb3-4742-bd21-c68ca01a818c"),"product 2","supplier 1", Double.valueOf(34.10));
        application.productRepository.save(product2);
        
    }
}
