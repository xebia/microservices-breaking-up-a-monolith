package com.xebia.catalog.rest;

import com.xebia.catalog.domain.Product;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.util.ArrayList;
import java.util.List;

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

    public List<ProductResource> toResource(List<Product> products) {
        List<ProductResource> result = new ArrayList<>();
        for (Product product:products) {
            result.add(toResource(product));
        }
        return result;
    }
}
