package com.xebia.msa;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.xebia.msa.domain.Account;
import com.xebia.msa.domain.LineItem;
import com.xebia.msa.domain.Product;
import com.xebia.msa.domain.ShoppingCart;
import com.xebia.msa.domain.WebUser;
import com.xebia.msa.repositories.AccountRepository;
import com.xebia.msa.repositories.LineItemRepository;
import com.xebia.msa.repositories.OrderRepository;
import com.xebia.msa.repositories.ProductRepository;
import com.xebia.msa.repositories.ShoppingCartRepository;
import com.xebia.msa.repositories.WebUserRepository;

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
	
    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        Application application = applicationContext.getBean(Application.class);

        WebUser user1 = new WebUser(UUID.fromString("1bb1e93f-a56d-4dad-b7d7-c24e28abb913"), "user1", "password");
        user1 = application.webUserRepository.save(user1);
        WebUser user2 = new WebUser(UUID.fromString("2bb1e93f-a56d-4dad-b7d7-c24e28abb913"), "user2", "password");
        user2 = application.webUserRepository.save(user2);

        Product product = new Product(UUID.randomUUID(),"product 1","supplier 1", new Double(112.10));
        product = application.productRepository.save(product);

        Product product2 = new Product(UUID.fromString("65c4cffb-1bb3-4742-bd21-c68ca01a818c"),"product 2","supplier 1", new Double(34.10));
        product2 = application.productRepository.save(product2);

        LineItem item1 = new LineItem(UUID.randomUUID(), 1, 10, product);
        item1 = application.lineItemRepository.save(item1);
        
        ShoppingCart cart = new ShoppingCart(new Date(), UUID.fromString("ac45c6c5-ccab-4eca-b6e2-5fcd18dd1056"));
        cart.addLineItem(item1);
        cart = application.shoppingCartRepository.save(cart);
        item1 = application.lineItemRepository.save(item1);

        user1.setShoppingCart(cart);
        user1 = application.webUserRepository.save(user1);

        Account account = new Account(UUID.fromString("f1482f17-82c2-4467-9080-174cb7019fe8"), "address " + 1, "+31355381921", "info@xebia.com");
        account = application.accountRepository.save(account);
        user1.setAccount(account);
        application.webUserRepository.save(user1);

        LOG.info("Read webuser back from database");
        WebUser webUser = application.webUserRepository.findByUuid(user1.getUuid());
        LOG.info(webUser.toString());

        LOG.info("*************");
        LOG.info("Access data by querying webuser: 'http://localhost:8080/monolith/user/1bb1e93f-a56d-4dad-b7d7-c24e28abb913'");
        LOG.info("User accounts: ");
        Iterable<WebUser> users = application.webUserRepository.findAll();
        for (WebUser user: users) {
            LOG.info("user: " + user);
        }
        LOG.info("*************");
        
    }

}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    WebUserRepository webUserRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                WebUser account = webUserRepository.findByUsername(username).get(0);
                if(account != null) {
                    return new User(account.getUsername(), account.getPassword(), true, true, true, true,
                            AuthorityUtils.createAuthorityList("USER"));
                } else {
                    throw new UsernameNotFoundException("could not find the user '"
                            + username + "'");
                }
            }

        };
    }
    
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/monolith/user/register", "/about").permitAll()
                .anyRequest().fullyAuthenticated().and().
                httpBasic().and().
                csrf().disable();
    }

}
