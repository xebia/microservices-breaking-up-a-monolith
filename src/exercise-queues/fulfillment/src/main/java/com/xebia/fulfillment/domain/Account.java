package com.xebia.fulfillment.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Account {
    @Id
    private UUID uuid;
    private String address;
    // TODO: we don't really need the phone number, remove?
    private String phoneNumber;
    private String email;

    public Account() {}
    public Account(UUID uuid, String address, String phoneNumber, String email) {
        this.uuid = uuid;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public Account(String address, String phoneNumber, String email) {
        this.uuid = UUID.randomUUID();
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Account{" +
                "uuid=" + uuid +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return uuid.equals(account.uuid);

    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
