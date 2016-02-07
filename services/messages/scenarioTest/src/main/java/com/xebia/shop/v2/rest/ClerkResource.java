package com.xebia.shop.v2.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xebia.shop.v2.domain.*;
import org.hibernate.annotations.Cascade;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ClerkResource extends ResourceSupport {

    private UUID uuid;
    private WebUser webUser;
    private int status;
    private Orderr orderr;
    private Payment payment;
    private Shipment shipment;
    private ShoppingCart shoppingCart;

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public ClerkResource(
            @JsonProperty(value = "uuid") @NotNull UUID uuid,
            @JsonProperty(value = "webUser") @NotNull WebUser webUser,
            @JsonProperty(value = "orderr") Orderr orderr,
            @JsonProperty(value = "status") int status,
            @JsonProperty(value = "payment") Payment payment,
            @JsonProperty(value = "shipment") Shipment shipment,
            @JsonProperty(value = "shoppingCart") ShoppingCart shoppingCart
    ) {
        this.uuid = uuid;
        this.webUser = webUser;
        this.orderr = orderr;
        this.status = status;
    }

    public ClerkResource() {}

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public WebUser getWebUser() {
        return webUser;
    }

    public void setWebUser(WebUser webUser) {
        this.webUser = webUser;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Orderr getOrderr() {
        return orderr;
    }

    public void setOrderr(Orderr orderr) {
        this.orderr = orderr;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    @Override
    public String toString() {
        return "ClerkResource{" +
                "uuid=" + uuid +
                "status=" + status +
                "orderr=" + orderr +
                "webUser=" + webUser +
                "cart=" + shoppingCart +
                "shipment=" + shipment +
                "payment=" + payment +
                '}';
    }
}
