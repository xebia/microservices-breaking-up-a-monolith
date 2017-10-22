package com.xebia.shopmanager.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xebia.shopmanager.domain.WebUser;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ClerkResource extends ResourceSupport {

    private UUID uuid;
    private WebUser webUser;
    private int status;
    private String document;

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public ClerkResource(
            @JsonProperty(value = "uuid") @NotNull UUID uuid,
            @JsonProperty(value = "webUser") @NotNull WebUser webUser,
            @JsonProperty(value = "status") int status,
            @JsonProperty(value = "document") String document
            ) {
        this.uuid = uuid;
        this.webUser = webUser;
        this.status = status;
        this.document = document;
    }

    public ClerkResource() {
        // Empty constructor required by framework
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public WebUser getWebUser() {
        return webUser;
    }

    public void setWebUser(WebUser webUser) {
        this.webUser = webUser;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getDocument() {
        return document;
    }

    @Override
    public String toString() {
        return "ClerkResource{" +
                "uuid=" + uuid +
                "status=" + status +
                "webUser=" + webUser +
                '}';
    }
}
