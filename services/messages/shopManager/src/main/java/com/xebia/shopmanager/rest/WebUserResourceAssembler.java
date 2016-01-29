package com.xebia.shopmanager.rest;


import com.xebia.shopmanager.domain.WebUser;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

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
