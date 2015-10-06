package com.xebia.shop.rest;


import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import com.xebia.shop.domain.ShoppingCart;

public class ShoppingCartResourceAssembler extends ResourceAssemblerSupport<ShoppingCart, ShoppingCartResource> {

    public ShoppingCartResourceAssembler() {
        super(ShoppingCartController.class, ShoppingCartResource.class);
    }

    @Override
    public ShoppingCartResource toResource(ShoppingCart cart) {

        ShoppingCartResource resource = createResourceWithId(cart.getUuid(), cart);
        BeanUtils.copyProperties(cart, resource);

        return resource;
    }

}
