package com.xebia.shop.v2.rest;

import com.xebia.shop.v2.domain.*;
import com.xebia.shop.v2.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/shop/v2/cart/")
public class ShoppingCartController {

    private static Logger LOG = LoggerFactory.getLogger(ShoppingCartController.class);

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LineItemRepository lineItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClerkRepository clerkRepository;

    private ShoppingCartResourceAssembler shoppingCartAssembler = new ShoppingCartResourceAssembler();
    private OrderResourceAssembler orderAssembler = new OrderResourceAssembler();

    @RequestMapping(method = RequestMethod.POST, value = "/{cartId}/add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ShoppingCartResource> addProductToShoppingCart(@PathVariable UUID cartId, @RequestBody NewLineItemResource newLineItemResource, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: cartId="+cartId.toString());
    	ShoppingCart cart = shoppingCartRepository.findOne(cartId);
        Product prod = productRepository.findOne(newLineItemResource.getProductId());
        LineItem lineItem = new LineItem(newLineItemResource.getQuantity(), prod.getPrice().doubleValue(), prod);
        lineItem = lineItemRepository.save(lineItem);
        cart.addLineItem(lineItem);
        cart = shoppingCartRepository.save(cart);
        ShoppingCartResource resource = shoppingCartAssembler.toResource(cart);
        return new ResponseEntity<ShoppingCartResource>(resource, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{cartId}/order", consumes = "application/json", produces = "application/json")
    public ResponseEntity<OrderResource> orderShoppingCart(@PathVariable UUID cartId, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: cartId="+cartId.toString());
        ShoppingCart cart = shoppingCartRepository.findOne(cartId);
        Clerk clerk = clerkRepository.findByShoppingCart(cart);
        Orderr orderr = new Orderr(cart);
        orderr = orderRepository.save(orderr);
        OrderResource resource = orderAssembler.toResource(orderr);
        clerk.setOrderr(orderr);
        clerkRepository.save(clerk);
        return new ResponseEntity<OrderResource>(resource, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{cartId}", produces = "application/json")
    public ResponseEntity<ShoppingCartResource> getCart(@PathVariable UUID cartId, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: cartId="+cartId.toString());
        ShoppingCart cart = shoppingCartRepository.findOne(cartId);
        ShoppingCartResource resource = shoppingCartAssembler.toResource(cart);
        return new ResponseEntity<ShoppingCartResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/forClerk/{clerkId}", produces = "application/json")
    public ResponseEntity<ShoppingCartResource> findCartByClerk(@PathVariable UUID clerkId, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: clerkId="+clerkId.toString());
        Clerk clerk = clerkRepository.findOne(clerkId);
        ShoppingCart cart = shoppingCartRepository.findOne(clerk.getShoppingCart().getUuid());
        ShoppingCartResource resource = shoppingCartAssembler.toResource(cart);
        return new ResponseEntity<ShoppingCartResource>(resource, HttpStatus.OK);
    }

}
