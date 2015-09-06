package com.xebia.shop.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
public class Orderr {
    @Id
    private UUID uuid;
    private Date ordered;
    private Date shipped;
    private String shippingAddress;
    private String status;
    private double total;
    private UUID paymentUuid;
    private boolean paymentReceived;

    @OneToOne(optional = false)
    private ShoppingCart shoppingCart;

    @OneToOne(optional = true)
    private Account account;

    public Orderr() {}
    public Orderr(UUID uuid, Date ordered, String shippingAddress, String status) {
        this.uuid = uuid;
        this.ordered = ordered;
        this.shippingAddress = shippingAddress;
        this.status = status;
    }

    public Orderr(Date ordered, String shippingAddress, String status) {
        this.uuid = UUID.randomUUID();
        this.ordered = ordered;
        this.shippingAddress = shippingAddress;
        this.status = status;
    }

    public Orderr(ShoppingCart cart) {
        this.uuid = UUID.randomUUID();
        this.ordered = cart.getCreated();
        this.status = "created";
        this.shippingAddress = "address";
        this.shoppingCart = cart;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
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

    public Date getOrdered() {
        return ordered;
    }

    public void setOrdered(Date ordered) {
        this.ordered = ordered;
    }

    public Date getShipped() {
        return shipped;
    }

    public void setShipped(Date shipped) {
        this.shipped = shipped;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotal() {
        return shoppingCart.getTotal();
    }

    public void setTotal(double total) {
        if (shoppingCart != null) {
            shoppingCart.setTotal(total);
        }
    }

    public UUID getPaymentUuid() {
		return paymentUuid;
	}
	public void setPaymentUuid(UUID paymentUuid) {
		this.paymentUuid = paymentUuid;
	}
	public boolean getPaymentReceived() {
        return paymentReceived;
    }

    public void setPaymentReceived(boolean paymentReceived) {
        this.paymentReceived = paymentReceived;
    }

    public boolean canBeApproved() {
        return getAccount()!=null;
    }

    @Override
    public String toString() {
        return "Orderr{" +
                "uuid=" + uuid +
                ", ordered=" + ordered +
                ", shipped=" + shipped +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", status='" + status + '\'' +
                ", total=" + total +
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
