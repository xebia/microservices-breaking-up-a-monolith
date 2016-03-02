package com.xebia.fulfillment.v2.domain;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Cascade;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
public class Clerk {
    public final static int FULFILLING = 0;

    @Id
    private UUID uuid;
    private int status = FULFILLING;
    @OneToOne(optional = true)
    @Cascade(value = {org.hibernate.annotations.CascadeType.MERGE})
    private Shipment shipment;

    public Clerk() {
    }

    public Clerk(UUID uuid, int status) {
        this.uuid = uuid;
        this.status = status;
    }

    public Clerk(JsonNode document) {
        setUuid(UUID.fromString(document.get("uuid").asText()));
        setStatus(Integer.parseInt(document.get("status").asText()));
        JsonNode shipmentNode = document.get("shipment");
        if (shipmentNode != null && !shipmentNode.asText().equals("null")) {
            this.setShipment(new Shipment(shipmentNode));
        }
    }

    public int getStatus() {
        return status;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setShipment(Shipment Shipment) {
        this.shipment = Shipment;
    }

    public Shipment getShipment() {
        return shipment;
    }

    @Override
    public String toString() {
        return "Clerk{" +
                "uuid='" + uuid + '\'' +
                ", status='" + status + '\'' + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clerk clerk = (Clerk) o;
        return uuid.equals(clerk.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

}
