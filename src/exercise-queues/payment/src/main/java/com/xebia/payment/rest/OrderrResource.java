package com.xebia.payment.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

public class OrderrResource {

    private UUID uuid;
    private UUID paymentUuid;
    private double total;
    private String description;

    @JsonCreator
    public OrderrResource(@JsonProperty(value = "uuid") @NotNull UUID uuid,
    					  @JsonProperty(value = "paymentUuid") @NotNull UUID paymentUuid,
    					  @JsonProperty(value = "total") double total,
                          @JsonProperty(value = "description") String description) {
        this.uuid = uuid;
        this.paymentUuid = paymentUuid;
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


    public UUID getPaymentUuid() {
		return paymentUuid;
	}


	public void setPaymentUuid(UUID paymentUuid) {
		this.paymentUuid = paymentUuid;
	}


	public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "OrderrResource{" +
                "uuid=" + uuid +
                ", total=" + total +
                ", description='" + description + '\'' +
                '}';
    }
}

