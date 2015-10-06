package com.xebia.shop.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.xebia.shop.domain.*;
import com.xebia.shop.repositories.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/shop/cart/")
public class ShoppingCartController {

    private static Logger LOG = LoggerFactory.getLogger(ShoppingCartController.class);

    @Autowired
    private WebUserRepository webUserRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LineItemRepository lineItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    private ShoppingCartResourceAssembler assembler = new ShoppingCartResourceAssembler();
    private OrderResourceAssembler orderAssembler = new OrderResourceAssembler();

    @RequestMapping(method = RequestMethod.POST, value="user/{userId}", produces = "application/json")
    public ResponseEntity<ShoppingCartResource> createNewShoppingCart(@PathVariable UUID userId, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: userId="+userId.toString());
        ShoppingCart cart = null;
        WebUser user = webUserRepository.findByUuid(userId);
        if (user.getShoppingCart() == null){
            cart = shoppingCartRepository.save(new ShoppingCart(new Date(), UUID.randomUUID()));
            user.setShoppingCart(cart);
            shoppingCartRepository.save(cart);
            webUserRepository.save(user);
        } else {
            return new ResponseEntity<ShoppingCartResource>(HttpStatus.NOT_ACCEPTABLE);
        }

        ShoppingCartResource resource = assembler.toResource(cart);

        return new ResponseEntity<ShoppingCartResource>(resource, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value ="user/{userId}", produces = "application/json")
    public ResponseEntity<ShoppingCartResource> showShoppingCartContents(@PathVariable UUID userId, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod());
        WebUser user = webUserRepository.findByUuid(userId);
        ShoppingCart cart = user.getShoppingCart();

        ShoppingCartResource resource = assembler.toResource(cart);

        return new ResponseEntity<ShoppingCartResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{cartId}/add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ShoppingCartResource> addProductToShoppingCart(@PathVariable UUID cartId, @RequestBody NewLineItemResource newLineItemResource, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: cartId="+cartId.toString());
    	ShoppingCart cart = shoppingCartRepository.findOne(cartId);
        //todo: validate that items are in stock
        Product prod = productRepository.findOne(newLineItemResource.getProductId());
        LineItem lineItem = new LineItem(newLineItemResource.getQuantity(), prod.getPrice().doubleValue(), prod);
        lineItem = lineItemRepository.save(lineItem);
        cart.addLineItem(lineItem);
        cart = shoppingCartRepository.save(cart);
        ShoppingCartResource resource = assembler.toResource(cart);
        return new ResponseEntity<ShoppingCartResource>(resource, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{cartId}/order", consumes = "application/json", produces = "application/json")
    public ResponseEntity<OrderResource> orderShoppingCart(@PathVariable UUID cartId, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: cartId="+cartId.toString());
        ShoppingCart cart = shoppingCartRepository.findOne(cartId);
        Orderr orderr = new Orderr(cart);
        orderr = orderRepository.save(orderr);
        OrderResource resource = orderAssembler.toResource(orderr);
        return new ResponseEntity<OrderResource>(resource, HttpStatus.CREATED);
    }

}
