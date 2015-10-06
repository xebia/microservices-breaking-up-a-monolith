package com.xebia.shop.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class OrderrResource {

    private UUID uuid;
    private double total;
    private String description;

    @JsonCreator
    public OrderrResource(@JsonProperty(value = "uuid") @NotNull UUID uuid,
                          @JsonProperty(value = "total") double total,
                          @JsonProperty(value = "description") String description) {
        this.uuid = uuid;
        this.total = total;
        this.description = description;
    }


    public OrderrResource() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }


}

