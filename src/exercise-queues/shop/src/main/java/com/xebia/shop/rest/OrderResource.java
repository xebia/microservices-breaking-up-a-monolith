package com.xebia.shop.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xebia.shop.domain.Account;
import com.xebia.shop.domain.ShoppingCart;

import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

public class OrderResource extends ResourceSupport {

    private UUID uuid;
    private Date ordered;
    private Date shipped;
    private String shippingAddress;
    private String status;
    private double total;
    private ShoppingCart shoppingCart;
    private Account account;
    private boolean paymentReceived;

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public OrderResource(@JsonProperty(value = "uuid") @NotNull UUID uuid,
                         @JsonProperty(value = "ordered") @NotNull Date ordered,
                         @JsonProperty(value = "shipped") Date shipped,
                         @JsonProperty(value = "shippingAddress") @NotNull String shippingAddress,
                         @JsonProperty(value = "status") @NotNull String status,
                         @JsonProperty(value = "total") double total,
                         @JsonProperty(value = "shoppingCart") @NotNull ShoppingCart shoppingCart,
                         @JsonProperty(value = "account") @NotNull Account account) {
        this.uuid = uuid;
        this.ordered = ordered;
        this.shipped = shipped;
        this.shippingAddress = shippingAddress;
        this.status = status;
        this.total = total;
        this.shoppingCart = shoppingCart;
        this.account = account;
    }

    @JsonCreator
    public OrderResource(@JsonProperty(value = "cart") @NotNull ShoppingCart cart) {
        this.uuid = UUID.randomUUID();
        this.ordered = cart.getCreated();
        this.status = "created";
        this.shippingAddress = "address";
        this.shoppingCart = cart;
    }

    public OrderResource() {
    }

    public boolean isPaymentReceived() {
        return paymentReceived;
    }

    public void setPaymentReceived(boolean paymentReceived) {
        this.paymentReceived = paymentReceived;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Date getOrdered() {
        return ordered;
    }

    public void setOrdered(Date ordered) {
        this.ordered = ordered;
    }

    public Date getShipped() {
        return shipped;
    }

    public void setShipped(Date shipped) {
        this.shipped = shipped;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}

