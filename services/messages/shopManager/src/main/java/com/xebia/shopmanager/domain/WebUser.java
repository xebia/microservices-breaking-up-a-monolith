package com.xebia.shopmanager.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
public class WebUser {
    @Id
    private UUID uuid;
    private String username;
    private String password;

    public WebUser() {
    }

    public WebUser(UUID uuid, String username, String password) {
        this.uuid = uuid;
        this.password = password;
        this.username = username;
    }

    public WebUser(String username, String password) {
        new WebUser(UUID.randomUUID(), username, password);
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
        return "WebUser{" +
                ", uuid='" + uuid + '\'' +
                "password='" + password + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebUser webUser = (WebUser) o;

        return uuid.equals(webUser.uuid);

    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
