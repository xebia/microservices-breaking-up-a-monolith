package com.xebia.shopmanager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;
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

    public WebUser(JsonNode node) {
        setUuid(UUID.fromString(node.get("uuid").asText()));
        setUsername(node.get("username").asText());
        setPassword(node.get("password").asText());
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

    public ObjectNode asJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.set("uuid", new TextNode(uuid.toString()));
        if (username != null) {
            node.set("username", new TextNode(username.toString()));
        }
        if (password != null) {
            node.set("password", new TextNode(password.toString()));
        }
        return node;
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
