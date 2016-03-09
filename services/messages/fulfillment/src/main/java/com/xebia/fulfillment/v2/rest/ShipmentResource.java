package com.xebia.fulfillment.v2.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.util.UUID;

// TODO: create test where this is used

public class ShipmentResource extends ResourceSupport {

    private UUID uuid;
    private String status;
    private String address;

    @JsonCreator
    public ShipmentResource(
            @JsonProperty(value = "uuid") @NotNull UUID uuid,
            @JsonProperty(value = "status") @NotNull String status,
            @JsonProperty(value = "address") @NotNull String address
    ) {
        this.uuid = uuid;
        this.status = status;
        this.address = address;
    }

    public ShipmentResource() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
