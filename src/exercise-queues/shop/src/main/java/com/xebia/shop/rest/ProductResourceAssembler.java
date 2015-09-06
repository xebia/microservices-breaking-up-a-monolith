package com.xebia.shop.rest;


import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import com.xebia.shop.domain.Product;

public class ProductResourceAssembler extends ResourceAssemblerSupport<Product, ProductResource> {

    public ProductResourceAssembler() {
        super(ProductController.class, ProductResource.class);
    }

    @Override
    public ProductResource toResource(Product product) {

        ProductResource resource = createResourceWithId(product.getUuid(), product);
        BeanUtils.copyProperties(product, resource);

        return resource;
    }

    public Product toClass(ProductResource resource){

        return new Product(resource.getUuid(), resource.getName(), resource.getPrice());

    }
}
