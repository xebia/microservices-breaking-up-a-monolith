package com.xebia.payment.v2.domain;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Cascade;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
public class Clerk {
    public final static int SHOPPING = 0;
    public final static int PAYING = 0;
    public final static int FULFILLING = 0;

    @Id
    private UUID uuid;
    private int status = SHOPPING;
    @OneToOne(optional = true)
    @Cascade(value = {org.hibernate.annotations.CascadeType.MERGE})
    private Payment payment;

    public Clerk() {
    }

    public Clerk(UUID uuid, int status) {
        this.uuid = uuid;
        this.status = status;
    }

    public Clerk(JsonNode document) {
        setUuid(UUID.fromString(document.get("uuid").asText()));
        setStatus(Integer.parseInt(document.get("status").asText()));
        JsonNode paymentNode = document.get("payment");
        if (paymentNode != null && paymentNode.asText()!="null") {
            this.setPayment(new Payment(paymentNode));
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

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
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
