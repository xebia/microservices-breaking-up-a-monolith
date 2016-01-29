package com.xebia.shopmanager.rest;


import com.xebia.shopmanager.domain.Clerk;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class ClerkResourceAssembler extends ResourceAssemblerSupport<Clerk, ClerkResource> {

    public ClerkResourceAssembler() {
        super(ClerkController.class, ClerkResource.class);
    }

    @Override
    public ClerkResource toResource(Clerk Clerk) {

        ClerkResource resource = createResourceWithId(Clerk.getUuid(), Clerk);
        BeanUtils.copyProperties(Clerk, resource);

        return resource;
    }

}
