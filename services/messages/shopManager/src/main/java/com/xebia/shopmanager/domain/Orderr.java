package com.xebia.shopmanager.domain;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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
    private boolean paymentReceived;

    public Orderr() {}

    public Orderr(UUID uuid, Date ordered, String shippingAddress, String status) {
        this.uuid = uuid;
        this.ordered = ordered;
        this.shippingAddress = shippingAddress;
        this.status = status;
    }

    public Orderr(Date ordered, String shippingAddress, String status) {
        this.uuid = UUID.randomUUID();
        this.ordered = ordered;
        this.shippingAddress = shippingAddress;
        this.status = status;
    }

    public Orderr(ShoppingCart cart) {
        this.uuid = UUID.randomUUID();
        this.ordered = cart.getCreated();
        this.status = "created";
        this.shippingAddress = "address";
    }

    public boolean isPaymentReceived() {
        return paymentReceived;
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

	public boolean getPaymentReceived() {
        return paymentReceived;
    }

    public void setPaymentReceived(boolean paymentReceived) {
        this.paymentReceived = paymentReceived;
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
