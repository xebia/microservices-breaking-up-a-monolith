package com.xebia.msa.rest;

import com.xebia.msa.domain.Product;
import com.xebia.msa.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/monolith/products")
public class ProductController {

    private static Logger LOG = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductRepository productRepository;

    private ProductResourceAssembler assembler = new ProductResourceAssembler();


    @RequestMapping(method = RequestMethod.GET)
    public List<ProductResource> allProducts() {
        List<ProductResource> orderResources = new ArrayList<ProductResource>();

        orderResources = assembler.toResources(productRepository.findAll());

        return orderResources;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<ProductResource> viewProduct(@PathVariable String id) {

        Product product = productRepository.findOne(UUID.fromString(id));

        if (product == null) {
            return new ResponseEntity<ProductResource>(HttpStatus.NOT_FOUND);
        }
        ProductResource resource = assembler.toResource(product);

        return new ResponseEntity<ProductResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<ProductResource> createNewProductInInventory(@RequestBody ProductResource productResource) {
        Product product = new Product(UUID.randomUUID(), productResource.getName(), productResource.getSupplier(), productResource.getPrice());
        Product responseProduct = productRepository.save(product);
        ProductResource responseResource = assembler.toResource(responseProduct);
        HttpHeaders headers = new HttpHeaders();
        headers.add("ETag", Integer.toString(responseProduct.hashCode()));
        headers.setLocation(linkTo(methodOn(getClass()).viewProduct(responseProduct.getUuid().toString())).toUri());
        return new ResponseEntity<ProductResource>(responseResource, HttpStatus.CREATED);
    }
}
