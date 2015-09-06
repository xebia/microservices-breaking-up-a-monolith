package com.xebia.shop.rest;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Date;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Created by marco on 31/08/15.
 */

public class PaymentResponse {

    private UUID uuid;
    private String description;

    @JsonCreator
    public PaymentResponse(@JsonProperty(value = "uuid") @NotNull UUID uuid,
                           @JsonProperty(value = "description") @NotNull String description) {
        this.uuid = uuid;
        this.description = description;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentResponse that = (PaymentResponse) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        return !(description != null ? !description.equals(that.description) : that.description != null);

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
