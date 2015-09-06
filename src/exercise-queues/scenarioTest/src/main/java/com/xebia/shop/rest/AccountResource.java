package com.xebia.shop.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class AccountResource extends ResourceSupport {

    private UUID uuid;
    private String address;
    private String phoneNumber;
    private String email;

    @JsonCreator
    public AccountResource(
            @JsonProperty(value = "uuid") @NotNull UUID uuid,
            @JsonProperty(value = "address") @NotNull String address,
            @JsonProperty(value = "phoneNumber") @NotNull String phoneNumber,
            @JsonProperty(value = "email") @NotNull String email) {
        this.uuid = uuid;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public AccountResource() {}

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
}
