package com.xebia.shop.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class NewLineItemResource extends ResourceSupport {

    private UUID productId;
    private Integer quantity;

    @JsonCreator
    public NewLineItemResource(
            @JsonProperty(value = "productId") @NotNull UUID productId,
            @JsonProperty(value = "quantity") @NotNull Integer quantity) {

        this.productId = productId;
        this.quantity = quantity;
    }

    public NewLineItemResource() {
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
