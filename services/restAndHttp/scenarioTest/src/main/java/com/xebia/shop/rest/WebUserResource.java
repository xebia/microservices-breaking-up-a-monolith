package com.xebia.shop.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xebia.shop.domain.Account;
import com.xebia.shop.domain.ShoppingCart;

import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.util.UUID;


public class WebUserResource extends ResourceSupport {

    private String password;
    private UUID uuid;

    private String username;
    private Account account;

    private ShoppingCart shoppingCart;

    @JsonCreator
    public WebUserResource(@JsonProperty(value = "username") @NotNull String username,
                           @JsonProperty(value = "password") @NotNull String password
    ) {
        this.uuid = UUID.randomUUID();
        this.username = username;
        this.password = password;
    }

    public WebUserResource() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

}

