package com.xebia.catalog.rest;

import com.xebia.catalog.domain.Product;
import com.xebia.catalog.repositories.ProductRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/product")
public class ProductController {

    private static Logger LOG = LoggerFactory.getLogger(ProductController.class);

    private ProductResourceAssembler productResourceAssembler = new ProductResourceAssembler();

    @Autowired
    private ProductRepository productRepository;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<ProductResource> newProduct(@RequestBody Product newProduct, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: "+newProduct.toString());
        Product product = productRepository.save(newProduct);
        ProductResource resource = new ProductResourceAssembler().toResource(product);
        return new ResponseEntity<ProductResource>(resource, HttpStatus.CREATED);
    }

    private static final DateFormat dataFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

    @RequestMapping(method = RequestMethod.GET, value = "/all", produces = "application/json")
    public ResponseEntity<ProductResource> getAllProducts(HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod());
        Iterable<Product> products = productRepository.findAll();
        List<Product> result = new ArrayList<Product>();
        for (Product product : products) {
            result.add(product);
        }
        List<ProductResource> productResources = new ProductResourceAssembler().toResource(result);
        return new ResponseEntity(productResources, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/random", produces = "application/json")
    public ResponseEntity<ProductResource> getRandomProduct(HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod());
        Iterable<Product> products = productRepository.findAll();
        List<Product> result = new ArrayList<Product>();
        for (Product product : products) {
            result.add(product);
        }
        if(result.size()>0){
	        int index = new Random().nextInt(result.size());
	        ProductResource productResource = new ProductResourceAssembler().toResource(result.get(index));
	        return new ResponseEntity(productResource, HttpStatus.OK);
        }
        else{
        	LOG.info("No products to randomize");
        	return new ResponseEntity(null, HttpStatus.NO_CONTENT); 	
        }
    }

}
