package com.xebia.shop.v2.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

public class PaymentResource extends ResourceSupport {
    private UUID uuid;
    private Date datePaid;
    private double total;
    private String description;
    private String cardId;
    private UUID orderUuid;

    @JsonCreator
    public PaymentResource(@JsonProperty(value = "uuid") @NotNull UUID uuid,
                           @JsonProperty(value = "total") @NotNull double total,
                           @JsonProperty(value = "description") @NotNull String description,
                           @JsonProperty(value = "orderUuid") @NotNull UUID orderUuid,
                           @JsonProperty(value = "datePaid") Date datePaid,
                           @JsonProperty(value = "cardId") String cardId) {
        this.uuid = uuid;
        this.datePaid = datePaid;
        this.total = total;
        this.description = description;
        this.cardId = cardId;
        this.orderUuid = orderUuid;
    }

    public PaymentResource(){
        // Empty constructor required by framework
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PaymentResource that = (PaymentResource) o;
        return !(uuid != null ? !uuid.equals(that.uuid) : that.uuid != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        return result;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Date getDatePaid() {
        return datePaid;
    }

    public void setDatePaid(Date datePaid) {
        this.datePaid = datePaid;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(UUID orderUuid) {
        this.orderUuid = orderUuid;
    }
}
