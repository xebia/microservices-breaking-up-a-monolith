package com.xebia.shop.rest;


import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import com.xebia.shop.domain.Account;

public class AccountResourceAssembler extends ResourceAssemblerSupport<Account, AccountResource> {

    public AccountResourceAssembler() {
        super(AccountController.class, AccountResource.class);
    }

    @Override
    public AccountResource toResource(Account account) {

        AccountResource resource = createResourceWithId(account.getUuid(), account);
        BeanUtils.copyProperties(account, resource);

        return resource;
    }

}
