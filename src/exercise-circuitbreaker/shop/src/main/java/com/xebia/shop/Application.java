package com.xebia.shop;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.xebia.shop.domain.*;
import com.xebia.shop.repositories.*;
import com.xebia.shop.rest.StartPaymentCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.Date;
import java.util.UUID;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {

    private static Logger LOG = LoggerFactory.getLogger(Application.class);

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


    public static void startMetricsMonitor() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    /**
                     * Since this is a simple example and we know the exact HystrixCommandKeys we are interested in
                     * we will retrieve the HystrixCommandMetrics objects directly.
                     *
                     * Typically you would instead retrieve metrics from where they are published which is by default
                     * done using Servo: https://github.com/Netflix/Hystrix/wiki/Metrics-and-Monitoring
                     */

                    // wait 5 seconds on each loop
                    try {
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        // ignore
                    }

                    // we are using default names so can use class.getSimpleName() to derive the keys
                    HystrixCommandMetrics callMetrics = HystrixCommandMetrics.getInstance(HystrixCommandKey.Factory.asKey(StartPaymentCommand.class.getSimpleName()));

                    // print out metrics
                    StringBuilder out = new StringBuilder();
                    out.append("\n");
                    out.append("#####################################################################################").append("\n");
                    out.append("# StartPaymentCommand: " + getStatsStringFromMetrics(callMetrics)).append("\n");
                    out.append("#####################################################################################").append("\n");
                    System.out.println(out.toString());
                }
            }

            private String getStatsStringFromMetrics(HystrixCommandMetrics metrics) {
                StringBuilder m = new StringBuilder();
                if (metrics != null) {
                    HystrixCommandMetrics.HealthCounts health = metrics.getHealthCounts();
                    m.append("Requests: ").append(health.getTotalRequests()).append(" ");
                    m.append("Errors: ").append(health.getErrorCount()).append(" (").append(health.getErrorPercentage()).append("%)   ");
                    //m.append("Mean: ").append(metrics.getExecutionTimePercentile(50)).append(" ");
                    //m.append("75th: ").append(metrics.getExecutionTimePercentile(75)).append(" ");
                    //m.append("90th: ").append(metrics.getExecutionTimePercentile(90)).append(" ");
                    //m.append("99th: ").append(metrics.getExecutionTimePercentile(99)).append(" ");
                }
                return m.toString();
            }

        });
        t.setDaemon(true);
        t.start();
    }

    public static void main(String[] args) {
    	//RestTemplate client  = new RestTemplate();
    	
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        Application application = applicationContext.getBean(Application.class);

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
        
        LOG.info("Read webuser back from database");
        WebUser webUser = application.webUserRepository.findByUuid(user1.getUuid());
        LOG.info(webUser.toString());

        startMetricsMonitor();

    }

}
