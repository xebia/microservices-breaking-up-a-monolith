package com.xebia.shop.v2.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xebia.shop.v2.domain.Orderr;
import com.xebia.shop.v2.domain.ShoppingCart;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

public class OrderResource extends ResourceSupport {

    // TODO: shipped and paymentReceived aren't used anymore -> remove
    private UUID uuid;
    private Date ordered;
    private Date shipped;
    private String shippingAddress;
    private String status;
    private double total;
    private boolean paymentReceived;

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public OrderResource(@JsonProperty(value = "uuid") @NotNull UUID uuid,
                         @JsonProperty(value = "ordered") @NotNull Date ordered,
                         @JsonProperty(value = "shipped") Date shipped,
                         @JsonProperty(value = "shippingAddress") @NotNull String shippingAddress,
                         @JsonProperty(value = "status") @NotNull String status,
                         @JsonProperty(value = "total") double total) {
        this.uuid = uuid;
        this.ordered = ordered;
        this.shipped = shipped;
        this.shippingAddress = shippingAddress;
        this.status = status;
        this.total = total;
    }

    @JsonCreator
    public OrderResource(@JsonProperty(value = "cart") @NotNull ShoppingCart cart) {
        this.uuid = UUID.randomUUID();
        this.ordered = cart.getCreated();
        this.status = "created";
        this.shippingAddress = "address";
    }

    public OrderResource() {
    }

    public OrderResource(Orderr orderr) {
        setUuid(orderr.getUuid());
        setOrdered(orderr.getOrdered());
        setShippingAddress(orderr.getShippingAddress());
        setStatus(orderr.getStatus());
        setTotal(orderr.getTotal());
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

}

