package com.xebia.msa.rest;


import com.xebia.msa.domain.Account;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

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
