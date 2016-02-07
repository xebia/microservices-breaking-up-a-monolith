package com.xebia.fulfillment.v2.domain;

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

    @OneToOne(optional = false)
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE})
    private WebUser webUser;
    @OneToOne(optional = true)
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE})
    private Orderr orderr;
    @OneToOne(optional = true)
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE})
    private Payment payment;
    @OneToOne(optional = true)
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE})
    private Shipment shipment;
    @OneToOne(optional = true)
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE})
    private ShoppingCart shoppingCart;

    public Clerk() {}
    public Clerk(WebUser webUser, UUID uuid) {
        this.webUser = webUser;
        this.uuid = uuid;
    }

    public Clerk(WebUser webUser) {
        this.webUser = webUser;
        this.uuid = UUID.randomUUID();
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }

    public Orderr getOrderr() {
        return orderr;
    }

    public void setOrderr(Orderr orderr) {
        this.orderr = orderr;
    }

    public int getStatus() {
        return status;
    }

    public WebUser getWebUser() {
        return webUser;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "Clerk{" +
                "uuid='" + uuid + '\'' +
                ", status='" + status + '\'' +
                ", orderr='" + orderr + '\'' +
                ", payment='" + payment + '\'' +
                ", cart='" + shoppingCart + '\'' +
                ", webuser='" + webUser + "'}";
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
