package com.xebia.shop.v2.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Shipment {
    @Id
    private UUID uuid;
    private String status;
    private String address;

    public static final String SHIPPABLE = "SHIPPABLE";
    public static final String SHIPPED = "SHIPPED";
    // TODO: remove after refactoring to Clerk is complete
    public static final String TO_BE_PAID = "TO_BE_PAID";

    public Shipment() {
    }

    public Shipment(UUID uuid, String status, String address) {
        this.uuid = uuid;
        this.status = status;
        this.address = address;
    }

    public void ship() throws InvalidStatusException {
        if (status.equals(SHIPPABLE)) {
            status = SHIPPED;
        } else {
            throw new InvalidStatusException(toString());
        }
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
                ", address='" + address + "'"  +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shipment shipment = (Shipment) o;

        return uuid.equals(shipment.uuid);

    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
