package com.xebia.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;


@Configuration
@EnableAutoConfiguration
@ComponentScan("com.xebia.frontend")
@SpringBootApplication
@RestController
public class ClerkWebAppInitializer {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Configuration
  @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
  protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .httpBasic()
          .and()
          .authorizeRequests()
          .antMatchers("/index.html", "/home.html", "/login.html", "/").permitAll()
          .anyRequest().authenticated().and()
          .csrf()
          .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }
  }

  @RequestMapping("/user")
  public Principal user(Principal user) {
    logger.info("/user");
    return user;
  }

  @RequestMapping("/token")
  @ResponseBody
  public Map<String,String> token(HttpSession session) {
    return Collections.singletonMap("token", session.getId());
  }

  public static void main(String[] args) throws Exception {
    SpringApplication.run(ClerkWebAppInitializer.class, args);
  }
}

