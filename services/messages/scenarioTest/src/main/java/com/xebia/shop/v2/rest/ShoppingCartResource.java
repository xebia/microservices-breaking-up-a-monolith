package com.xebia.shop.v2.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xebia.shop.v2.domain.LineItem;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ShoppingCartResource extends ResourceSupport {

    private Date created;
    private List<LineItem> lineItems = new ArrayList();
    private UUID uuid;

    @JsonCreator
    public ShoppingCartResource(@JsonProperty(value = "uuid") @NotNull UUID uuid,
                                @JsonProperty(value = "created") @NotNull Date created,
                                @JsonProperty(value = "lineItems") @NotNull List<LineItem> lineItems) {
        this.uuid = uuid;
        this.created = created;
        this.lineItems = lineItems;
    }

    public ShoppingCartResource() {
        // Empty constructor required by framework
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}

