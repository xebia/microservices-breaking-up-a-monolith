package com.xebia.shop.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.xebia.shop.domain.Product;
import com.xebia.shop.repositories.ProductRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/shop/products")
public class ProductController {

    private static Logger LOG = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductRepository productRepository;
    private ProductResourceAssembler assembler = new ProductResourceAssembler();

    @RequestMapping(method = RequestMethod.GET)
    public List<ProductResource> allProducts(HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod());
        List<ProductResource> orderResources = new ArrayList<ProductResource>();

        orderResources = assembler.toResources(productRepository.findAll());

        return orderResources;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<ProductResource> viewProduct(@PathVariable String id, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod());
        Product product = productRepository.findOne(UUID.fromString(id));

        if (product == null) {
            return new ResponseEntity<ProductResource>(HttpStatus.NOT_FOUND);
        }
        ProductResource resource = assembler.toResource(product);

        return new ResponseEntity<ProductResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<ProductResource> createNewProductInInventory(@RequestBody ProductResource productResource, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: "+productResource.toString());
        Product product = new Product(UUID.randomUUID(), productResource.getName(), productResource.getPrice());
        Product responseProduct = productRepository.save(product);
        ProductResource responseResource = assembler.toResource(responseProduct);
        HttpHeaders headers = new HttpHeaders();
        headers.add("ETag", Integer.toString(responseProduct.hashCode()));
        headers.setLocation(linkTo(methodOn(getClass()).viewProduct(responseProduct.getUuid().toString(), request)).toUri());
        return new ResponseEntity<ProductResource>(responseResource, HttpStatus.CREATED);
    }
}
