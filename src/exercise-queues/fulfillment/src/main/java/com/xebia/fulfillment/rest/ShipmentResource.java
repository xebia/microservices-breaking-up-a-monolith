package com.xebia.fulfillment.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xebia.fulfillment.domain.Orderr;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ShipmentResource extends ResourceSupport {

    private UUID uuid;
    private String status;
    private String address;
    private Orderr orderr;

    @JsonCreator
    public ShipmentResource(
            @JsonProperty(value = "uuid") @NotNull UUID uuid,
            @JsonProperty(value = "status") @NotNull String status,
            @JsonProperty(value = "address") @NotNull String address,
            @JsonProperty(value = "orderr") @NotNull Orderr orderr
            ) {
        this.uuid = uuid;
        this.status = status;
        this.address = address;
        this.orderr = orderr;
    }

    public ShipmentResource() {}

    public Orderr getOrderr() {
        return orderr;
    }

    public void setOrderr(Orderr orderr) {
        this.orderr = orderr;
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
