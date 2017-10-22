package com.xebia.catalog.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

public class ProductResource extends ResourceSupport {
    private UUID uuid;
    private String name;
    private String supplier;
    private double price;
    private Date dateAdded;

    @JsonCreator
    public ProductResource(@JsonProperty(value = "uuid") @NotNull UUID uuid,
                           @JsonProperty(value = "name") @NotNull String name,
                           @JsonProperty(value = "supplier") @NotNull String supplier,
                           @JsonProperty(value = "price") @NotNull double price,
                           @JsonProperty(value = "dateAdded") Date dateAdded) {
        this.uuid = uuid;
        this.name = name;
        this.supplier = supplier;
        this.price = price;
        this.dateAdded = new Date();
    }

    public ProductResource() {
        // Empty constructor required by framework
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public String toString() {
        return "ProductResource{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", supplier='" + supplier + '\'' +
                ", price=" + price +
                ", dateAdded=" + dateAdded +
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
        if (!super.equals(o)) {
            return false;
        }

        ProductResource that = (ProductResource) o;

        return !(uuid != null ? !uuid.equals(that.uuid) : that.uuid != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        return result;
    }
}
