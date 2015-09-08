package com.xebia.msa.rest;


import com.xebia.msa.domain.Product;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

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

        return new Product(resource.getUuid(), resource.getName(), resource.getSupplier(), resource.getPrice());

    }
}
