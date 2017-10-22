package com.xebia.fulfillment.v2.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Shipment {
    @Id
    private UUID uuid;
    private String status;
    private String address;

    public static final String SHIPPED = "SHIPPED";

    public Shipment() {
        // Empty constructor required by framework
    }

    public Shipment(UUID uuid, String status, String address) {
        this.uuid = uuid;
        this.status = status;
        this.address = address;
    }

    public Shipment(JsonNode node) {
        setUuid(UUID.fromString(node.get("uuid").asText()));
        setAddress(node.get("address").asText());
        setStatus(node.get("status").asText());
    }

    public Shipment(UUID uuid) {
        this.uuid = uuid;
    }

    public void ship() {
        status = SHIPPED;
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

    @Override
    public String toString() {
        return "Shipment{" +
                "uuid=" + uuid +
                ", status='" + status + "'" +
                ", address='" + address + "'" +
                '}';
    }

    public ObjectNode asJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.set("uuid", new TextNode(uuid.toString()));
        if (status != null) {
            node.set("status", new TextNode(status));
        }
        if (address != null) {
            node.set("address", new TextNode(address));
        }
        return node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Shipment shipment = (Shipment) o;
        return uuid.equals(shipment.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
