package com.xebia.shop;

import java.util.Date;
import java.util.UUID;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.xebia.shop.domain.Account;
import com.xebia.shop.domain.LineItem;
import com.xebia.shop.domain.Product;
import com.xebia.shop.domain.ShoppingCart;
import com.xebia.shop.domain.WebUser;
import com.xebia.shop.repositories.AccountRepository;
import com.xebia.shop.repositories.LineItemRepository;
import com.xebia.shop.repositories.OrderRepository;
import com.xebia.shop.repositories.ProductRepository;
import com.xebia.shop.repositories.ShoppingCartRepository;
import com.xebia.shop.repositories.WebUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableSwagger
public class ShopApplication {

    private static Logger LOG = LoggerFactory.getLogger(ShopApplication.class);
    @Value("${rabbitmq.hostname}")
    private String hostname="localhost";

    @Value("${rabbitmq.port}")
    private String port="5672";

    @Value("${rabbitmq.username}")
    private String username="guest";

    @Value("${rabbitmq.password}")
    private String password="guest";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(hostname, Integer.parseInt(port));
        return connectionFactory;
    }

    @Autowired
    private WebUserRepository webUserRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private LineItemRepository lineItemRepository;
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
                        "Shop API",
                        "This page provides details of the REST API for the Shop service",
                        "Go and explore ...",
                        null,
                        null,
                        null
                ))
                .useDefaultResponseMessages(false)
                        //Map the specific URL patterns into Swagger
                .includePatterns("/.*")
                .genericModelSubstitutes(ResponseEntity.class);
    }

    @Bean
    public ServletRegistrationBean hystrixStreamServlet(){
        return new ServletRegistrationBean(new HystrixMetricsStreamServlet(), "/hystrix.stream");
    }

    @Bean
    public FilterRegistrationBean commonsRequestLoggingFilter()
    {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new CommonsRequestLoggingFilter());
        return registrationBean;
    }


    public static void main(String[] args) {
    	//RestTemplate client  = new RestTemplate();
    	
        ApplicationContext applicationContext = SpringApplication.run(ShopApplication.class, args);
        ShopApplication application = applicationContext.getBean(ShopApplication.class);

        WebUser user1 = new WebUser(UUID.fromString("1bb1e93f-a56d-4dad-b7d7-c24e28abb913"), "user1", "password");
        user1 = application.webUserRepository.save(user1);
        WebUser user2 = new WebUser(UUID.fromString("2bb1e93f-a56d-4dad-b7d7-c24e28abb913"), "user2", "password");
        user2 = application.webUserRepository.save(user2);
        
        Product product = new Product(UUID.randomUUID(),"product 1", new Double(112.10));
        product = application.productRepository.save(product);
        Product product2 = new Product(UUID.fromString("65c4cffb-1bb3-4742-bd21-c68ca01a818c"),"product 1", new Double(34.50));
        product2 = application.productRepository.save(product2);

        LineItem item1 = new LineItem(UUID.randomUUID(), 1, 10, product);
        item1 = application.lineItemRepository.save(item1);
        item1.setProduct(product);
        item1 = application.lineItemRepository.save(item1);

        ShoppingCart cart = new ShoppingCart(new Date(), UUID.fromString("ac45c6c5-ccab-4eca-b6e2-5fcd18dd1056"));
        cart.addLineItem(item1);
        cart = application.shoppingCartRepository.save(cart);
        item1 = application.lineItemRepository.save(item1);

        user1.setShoppingCart(cart);
        user1 = application.webUserRepository.save(user1);

        Account account = new Account(UUID.randomUUID(), "address " + 1, "+31355381921", "info@xebia.com");
        account = application.accountRepository.save(account);
        user1.setAccount(account);
        application.webUserRepository.save(user1);
        
        /*
        Orderr orderr = new Orderr();
        orderr.setUuid(UUID.fromString("b20d9560-6003-4da0-a072-4d35c96cc0d1"));
        orderr.setShoppingCart(cart);
        orderr.setAccount(account);
        application.orderRepository.save(orderr);
        */
        
        LOG.info("Read webuser back from database");
        WebUser webUser = application.webUserRepository.findByUuid(user1.getUuid());
        LOG.info(webUser.toString());
        
    }

}
