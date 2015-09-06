package com.xebia.payment.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class LineItem {
    @Id
    private UUID uuid;
    private int quantity;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private Orderr orderr;
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Product product;
    private double price;

    public LineItem() {
    }

    public LineItem(UUID uuid, int quantity, double price, Product product) {
        this.uuid = uuid;
        this.quantity = quantity;
        this.price = price;
        this.product = product;
    }

    public LineItem(int quantity, double price, Product product) {
        this.uuid = UUID.randomUUID();
        this.quantity = quantity;
        this.price = price;
        this.product = product;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @JsonBackReference
    public Orderr getOrderr() {
        return orderr;
    }

    public void setOrderr(Orderr orderr) {
        this.orderr = orderr;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "LineItem{" +
                "uuid=" + uuid +
                ", quantity=" + quantity +
                ", orderrId=" + orderr.getUuid() +
                ", product=" + product +
                ", price=" + price +
                '}';
    }
}
