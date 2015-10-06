package com.xebia.fulfillment.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xebia.fulfillment.domain.Account;
import com.xebia.fulfillment.domain.LineItem;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderResource extends ResourceSupport {

    private UUID uuid;
    private String shippingAddress;
    private List<LineItem> lineItems = new ArrayList<LineItem>();
    private Account account;

    @JsonCreator
    public OrderResource(@JsonProperty(value = "uuid") @NotNull UUID uuid,
                         @JsonProperty(value = "shippingAddress") @NotNull String shippingAddress,
                         @JsonProperty(value = "lineItems") @NotNull List<LineItem> lineItems,
                         @JsonProperty(value = "account") @NotNull Account account) {
        this.uuid = uuid;
        this.shippingAddress = shippingAddress;
        this.lineItems = lineItems;
        this.account = account;
    }

    public OrderResource() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}

