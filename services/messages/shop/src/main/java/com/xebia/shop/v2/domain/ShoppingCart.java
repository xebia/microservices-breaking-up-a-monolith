package com.xebia.shop.v2.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class ShoppingCart {
    @Id
    private UUID uuid;
    private Date created;

    @OneToMany(mappedBy = "shoppingCart", targetEntity = LineItem.class)
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL})
    @JsonManagedReference
    private List<LineItem> lineItems = new ArrayList<LineItem>();
    @OneToOne(fetch= FetchType.LAZY)
    @JsonBackReference
    private Clerk clerk;

    public ShoppingCart() {
    }

    public ShoppingCart(Date created, UUID uuid) {
        this.uuid = uuid;
        this.created = created;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<LineItem> newItems) { lineItems = newItems; }
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void removeItem(int itemNumber) {
        lineItems.remove(itemNumber);
    }

    public void addLineItem(LineItem lineItem) {
        lineItem.setShoppingCart(this);
        lineItems.add(lineItem);
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Clerk getClerk() {
        return clerk;
    }

    public void setClerk(Clerk clerk) {
        this.clerk = clerk;
    }

    public double calcTotal() {
        double total = 0;
        for (LineItem lineItem: lineItems) {
            total += lineItem.getPrice();
        }
        return total;
    }

    public double getTotal() {
        return this.total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    private double total;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShoppingCart that = (ShoppingCart) o;

        return uuid.equals(that.uuid);

    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "ShoppingCart{" +
                ", uuid=" + uuid +
                "created=" + created +
                '}';
    }
}
