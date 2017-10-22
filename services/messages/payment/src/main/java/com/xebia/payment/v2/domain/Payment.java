package com.xebia.payment.v2.domain;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

@Entity
public class Payment {
    @Id
    private UUID uuid;
    private Date datePaid;
    private double total;
    private String cardId;
    private String description;

    public Payment(UUID uuid) {
        this.uuid = uuid;
    }

    public Payment(UUID uuid, Date datePaid, double total, String description, String cardId) {
        this.uuid = uuid;
        this.datePaid = datePaid;
        this.total = total;
        this.cardId = cardId;
        this.description = description;
    }

    public Payment() {
        // Empty constructor required by framework
    }

    public Payment(JsonNode node) {
        setUuid(UUID.fromString(node.get("uuid").asText()));
        setDescription(node.get("description").asText());
        setCardId(node.get("cardId").asText());
        setDatePaid(new Date(node.get("datePaid").asLong()));
        setTotal(node.get("total").asDouble());
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Payment payment = (Payment) o;
        return uuid.equals(payment.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "Payment{" +
                "uuid=" + uuid +
                ", datePaid=" + datePaid +
                ", total=" + total +
                ", cardId='" + cardId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public ObjectNode asJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.set("uuid", new TextNode(uuid.toString()));
        if (datePaid != null) {
            node.set("datePaid", new LongNode(datePaid.getTime()));
        }
        node.set("total", new DoubleNode(total));
        node.set("cardId", new TextNode(cardId));
        if (description != null) {
            node.set("description", new TextNode(description));
        }
        return node;
    }
}
