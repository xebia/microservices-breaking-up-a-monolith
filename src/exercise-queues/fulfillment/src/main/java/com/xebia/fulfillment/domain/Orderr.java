package com.xebia.fulfillment.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Exercise
// An Orderr as far as Payment is concerned is no more than a total amount, a UUID and a description
// This Orderr was copied from Shop and therefore contains a lot of unnecessary detail.
@Entity
public class Orderr {
    @Id
    private UUID uuid;
    private String shippingAddress;
    private boolean paymentReceived = false;
    @JsonManagedReference
    @OneToMany(mappedBy = "orderr", targetEntity = LineItem.class, cascade = CascadeType.ALL)
    private List<LineItem> lineItems = new ArrayList<LineItem>();
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Account account;

    public Orderr() {}
    public Orderr(UUID uuid, String shippingAddress, Account account) {
        this.uuid = uuid;
        this.shippingAddress = shippingAddress;
        this.account = account;
    }

    public boolean isPaymentReceived() {
        return paymentReceived;
    }

    public void setPaymentReceived(boolean paymentReceived) {
        this.paymentReceived = paymentReceived;
    }


    public List<LineItem> getLineItems() {
        return lineItems;
    }
    //@JsonManagedReference
    public void addLineItem(LineItem lineItem) {
        lineItem.setOrderr(this);
        lineItems.add(lineItem);
    }
    //@JsonManagedReference
    public void addLineItems(List<LineItem> lineItems) {
        for (LineItem item: lineItems){
            item.setOrderr(this);
            lineItems.add(item);
        }

    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @Override
    public String toString() {
        return "Orderr{" +
                "uuid=" + uuid +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", paymentReceived=" + paymentReceived +
                ", lineItems=" + lineItems +
                ", account=" + account +
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
