package com.xebia.catalog.rest;

import com.xebia.catalog.domain.Product;
import com.xebia.catalog.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    private ProductResourceAssembler productResourceAssembler = new ProductResourceAssembler();

    @Autowired
    private ProductRepository productRepository;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<ProductResource> newProduct(@RequestBody Product newProduct, HttpServletRequest request) {
        LOG.info("URL: " + request.getRequestURL() + ", METHOD: " + request.getMethod() + ", CONTENT: " + newProduct.toString());
        Product product = productRepository.save(newProduct);
        ProductResource resource = new ProductResourceAssembler().toResource(product);
        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", produces = "application/json")
    public ResponseEntity<ProductResource> getAllProducts(HttpServletRequest request) {
        LOG.info("URL: " + request.getRequestURL() + ", METHOD: " + request.getMethod());
        Iterable<Product> products = productRepository.findAll();
        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            result.add(product);
        }
        List<ProductResource> productResources = productResourceAssembler.toResource(result);
        return new ResponseEntity(productResources, HttpStatus.OK);
    }

}
