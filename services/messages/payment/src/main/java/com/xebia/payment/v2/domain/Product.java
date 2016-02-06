package com.xebia.payment.v2.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Product {

    @Id
    private UUID uuid;
    private String name;
    private String supplier;
    private double price;

    public Product() {}

    public Product(UUID uuid, String name, String supplier, Double price) {
        this.uuid = uuid;
        this.name = name;
        this.supplier = supplier;
        this.price = price.doubleValue();
    }

    public Product(String name, String supplier, Double price) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.supplier = supplier;
        this.price = price.doubleValue();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getSupplier() {
        return supplier;
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

        return supplier.equals(product.supplier);

    }

    @Override
    public int hashCode() {
        return supplier.hashCode();
    }

    @Override
    public String toString() {
        return "Product{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", supplier='" + supplier + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}

