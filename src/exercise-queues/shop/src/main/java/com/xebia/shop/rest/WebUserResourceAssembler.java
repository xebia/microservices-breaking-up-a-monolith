package com.xebia.shop.rest;


import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import com.xebia.shop.domain.WebUser;

public class WebUserResourceAssembler extends ResourceAssemblerSupport<WebUser, WebUserResource> {

    public WebUserResourceAssembler() {
        super(WebUserController.class, WebUserResource.class);
    }

    @Override
    public WebUserResource toResource(WebUser webuser) {

        WebUserResource resource = createResourceWithId(webuser.getUuid(), webuser);
        BeanUtils.copyProperties(webuser, resource);

        return resource;
    }

}
