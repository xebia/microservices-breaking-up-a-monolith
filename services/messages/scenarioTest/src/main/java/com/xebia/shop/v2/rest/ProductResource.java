package com.xebia.shop.v2.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ProductResource extends ResourceSupport {

    private UUID uuid;
    private String name;
    private String supplier;
    private Double price;

    @JsonCreator
    public ProductResource(@JsonProperty(value = "uuid") @NotNull UUID uuid,
                           @JsonProperty(value = "name") @NotNull String name,
                           @JsonProperty(value = "supplier") @NotNull String supplier,
                           @JsonProperty(value = "price") @NotNull Double price) {
        this.uuid = uuid;
        this.name = name;
        this.supplier = supplier;
        this.price = price;
    }

    public ProductResource() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    @Override
    public String toString() {
        return "ProductResource{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", supplier='" + supplier + '\'' +
                ", price=" + price +
                '}';
    }
}

