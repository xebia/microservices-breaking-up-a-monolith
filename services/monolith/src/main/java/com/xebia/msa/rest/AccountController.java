package com.xebia.msa.rest;

import com.xebia.msa.domain.Account;
import com.xebia.msa.domain.WebUser;
import com.xebia.msa.repositories.AccountRepository;
import com.xebia.msa.repositories.WebUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/monolith/account")
public class AccountController {

    private static Logger LOG = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private WebUserRepository webUserRepository;

    @Autowired
    private AccountRepository accountRepository;

    private AccountResourceAssembler accountResourceAssembler = new AccountResourceAssembler();

    @RequestMapping(method = RequestMethod.POST, value = "/user/{userId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AccountResource> createAccount(@PathVariable UUID userId, @RequestBody AccountResource accountResource) {
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
