package com.xebia.shop.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.xebia.shop.domain.Account;
import com.xebia.shop.domain.WebUser;
import com.xebia.shop.repositories.AccountRepository;
import com.xebia.shop.repositories.WebUserRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/shop/accounts")
public class AccountController {

    private static Logger LOG = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private WebUserRepository webUserRepository;

    @Autowired
    private AccountRepository accountRepository;

    private AccountResourceAssembler accountResourceAssembler = new AccountResourceAssembler();

    @RequestMapping(method = RequestMethod.POST, value = "/user/{userId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AccountResource> createAccount(@PathVariable UUID userId, @RequestBody AccountResource accountResource, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: "+accountResource.toString());
        WebUser user = webUserRepository.findByUuid(userId);
        if (user == null) {
            return new ResponseEntity<AccountResource>(HttpStatus.NOT_FOUND);
        }
        if (user.getAccount() != null) {
            return new ResponseEntity<AccountResource>(HttpStatus.FORBIDDEN);
        }
        Account account = new Account(accountResource.getAddress(), accountResource.getPhoneNumber(), accountResource.getEmail());
        accountRepository.save(account);
        user.setAccount(account);
        webUserRepository.save(user);
        AccountResource resource = accountResourceAssembler.toResource(account);

        return new ResponseEntity<AccountResource>(resource, HttpStatus.CREATED);
    }


}
