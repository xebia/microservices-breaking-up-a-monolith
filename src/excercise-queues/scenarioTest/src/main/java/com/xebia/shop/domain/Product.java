package com.xebia.shop.domain;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Product {

    @Id
    private UUID uuid;
    private String name;
    private double price;

    public Product() {}

    public Product(UUID uuid, String name, Double price) {
        this.uuid = uuid;
        this.name = name;
        this.price = price.doubleValue();
    }

    public Product(String name, Double price) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.price = price.doubleValue();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return new Double(price);
    }

    public void setPrice(Double price) {
        this.price = price.doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return uuid.equals(product.uuid);

    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "Product{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}

