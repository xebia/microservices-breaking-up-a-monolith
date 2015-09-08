package com.xebia.msa.domain;


import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
public class Payment {
    @Id
    private UUID uuid;
    private Date paid;
    private double total;
    private String details;

    public Payment(UUID uuid, Date paid, double total, String details) {
        this.uuid = uuid;
        this.paid = paid;
        this.total = total;
        this.details = details;
    }

    public Payment() {}

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Date getPaid() {
        return paid;
    }

    public void setPaid(Date paid) {
        this.paid = paid;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

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
                ", paid=" + paid +
                ", total=" + total +
                ", details='" + details + '\'' +
                '}';
    }
}
