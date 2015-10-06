package com.xebia.msa.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Collection;
import java.util.UUID;

@Entity
public class WebUser implements UserDetails {
    @Id
    private UUID uuid;
    private String username;
    private String password;

    @OneToOne(optional = true)
    private Account account;

    @OneToOne(optional = true)
    private ShoppingCart shoppingCart;

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

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
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
                "password='" + password + '\'' +
                ", uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", customer=" + account +
                ", shoppingCart=" + shoppingCart +
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
