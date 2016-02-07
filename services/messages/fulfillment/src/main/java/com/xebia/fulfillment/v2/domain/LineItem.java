package com.xebia.fulfillment.v2.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class LineItem {
    @Id
    private UUID uuid;
    private int quantity;
    private double price;

    @ManyToOne(fetch= FetchType.LAZY)
    @JsonBackReference
    private ShoppingCart shoppingCart;
    @OneToOne(optional = false)
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE})
    private Product product;

    public LineItem() {}

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

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
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
                "quantity=" + quantity +
               ", shoppingCartId=" + shoppingCart.getUuid() +
                ", product=" + product +
                ", price=" + price +
                '}';
    }
}
