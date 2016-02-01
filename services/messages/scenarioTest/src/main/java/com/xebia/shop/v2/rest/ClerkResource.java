package com.xebia.shop.v2.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xebia.shop.v2.domain.WebUser;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ClerkResource extends ResourceSupport {

    private UUID uuid;
    private WebUser webUser;
    private int status;
    private String orderr;

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public ClerkResource(
            @JsonProperty(value = "uuid") @NotNull UUID uuid,
            @JsonProperty(value = "webUser") @NotNull WebUser webUser,
            @JsonProperty(value = "orderr") String orderr,
                    @JsonProperty(value = "status") int status
    ) {
        this.uuid = uuid;
        this.webUser = webUser;
        this.orderr = orderr;
        this.status = status;
    }

    public ClerkResource() {}

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

    public String getOrderr() {
        return orderr;
    }

    public void setOrderr(String orderr) {
        this.orderr = orderr;
    }

    @Override
    public String toString() {
        return "ClerkResource{" +
                "uuid=" + uuid +
                "status=" + status +
                "orderr=" + orderr +
                "webUser=" + webUser +
                '}';
    }
}
