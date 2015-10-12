package com.xebia.payment.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Orderr {
    @Id
    private UUID uuid;
    private String shippingAddress;

    public Orderr() {}

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Orderr(UUID uuid, String shippingAddress) {
        this.uuid = uuid;
        this.shippingAddress = shippingAddress;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "Orderr{" +
                "uuid=" + uuid +
                ", shippingAddress='" + shippingAddress +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Orderr orderr = (Orderr) o;

        return uuid.equals(orderr.uuid);

    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

}
