package com.xebia.shop.v2.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

@Entity
public class Orderr {
    @Id
    private UUID uuid;
    private Date ordered;
    private String shippingAddress;
    private String status;
    private double total;

    public Orderr() {
        // Empty constructor required by framework
    }

    public Orderr(UUID uuid, Date ordered, String status) {
        this.uuid = uuid;
        this.ordered = new Date(ordered.getTime());
        this.status = status;
    }

    public Orderr(Date ordered, String status) {
        this.uuid = UUID.randomUUID();
        this.ordered = new Date(ordered.getTime());
        this.status = status;
    }

    public Orderr(ShoppingCart cart) {
        this.uuid = UUID.randomUUID();
        this.ordered = new Date(cart.getCreated().getTime());
        this.status = "created";
    }

    public boolean canBeApproved() {
        return shippingAddress != null;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Date getOrdered() {
        return ordered;
    }

    public void setOrdered(Date ordered) {
        this.ordered = ordered;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Orderr{" +
                "uuid=" + uuid +
                ", ordered=" + ordered +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", status='" + status + '\'' +
                ", total=" + total +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Orderr orderr = (Orderr) o;
        return uuid.equals(orderr.uuid);

    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

}
