package com.xebia.shopmanager.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.util.UUID;


public class WebUserResource extends ResourceSupport {


    private UUID uuid;
    private String password;
    private String username;

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public WebUserResource(@JsonProperty(value = "username") @NotNull String username,
                           @JsonProperty(value = "password") @NotNull String password
    ) {
        this.uuid = UUID.randomUUID();
        this.username = username;
        this.password = password;
    }


    public WebUserResource() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "WebUserResource{" +
                "uuid=" + uuid +
                ", username='" + username +
                '}';
    }
}

