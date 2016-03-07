package com.xebia.shopmanager.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

@Entity
public class Clerk {
    // TODO: set status and test.
    public final static int SHOPPING = 0;
    public final static int PAYING = 0;
    public final static int FULFILLING = 0;

    private transient ObjectMapper mapper = new ObjectMapper();

    @Id
    private UUID uuid;
    private int status = SHOPPING;
    @OneToOne(optional = false)
    @Cascade(value = {org.hibernate.annotations.CascadeType.MERGE})
    private WebUser webUser;
    @Column(columnDefinition = "clob")
    @Lob
    private String document;

    public Clerk() {
    }

    public Clerk(WebUser webUser, UUID uuid) throws Exception {
        this.webUser = webUser;
        this.uuid = uuid;
        this.document = "{\"uuid\":\""+uuid+"\",\"status\":"+status+"}";
        getDocument();
    }

    public Clerk(WebUser webUser) throws Exception {
        this.webUser = webUser;
        this.uuid = UUID.randomUUID();
        this.document = "{\"uuid\":\""+uuid+"\",\"status\":"+status+"}";
        getDocument();
    }

    public Clerk(String document) throws Exception {
        loadFromString(document);
    }

    public Clerk(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[fis.available()];
        fis.read(data);
        fis.close();
        loadFromString(new String(data));
    }

    private void loadFromString(String document) throws Exception {
        this.document = document;
        JsonNode jsonDocument = mapper.readValue(document, JsonNode.class);
        this.uuid = UUID.fromString(jsonDocument.get("uuid").asText());
        this.status = Integer.parseInt(jsonDocument.get("status").asText());
        JsonNode webUserNode = jsonDocument.get("webUser");
        if (webUserNode != null && webUserNode.asText() != "null") {
            this.setWebUser(new WebUser(webUserNode));
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setWebUser(WebUser webUser) {
        this.webUser = webUser;
    }

    public WebUser getWebUser() {
        return webUser;
    }

    public String getDocument() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode parsedDocument = (ObjectNode) mapper.readValue(document, JsonNode.class);
        parsedDocument.replace("webUser", this.webUser.asJson());
        this.document = mapper.writeValueAsString(parsedDocument);
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "Clerk{" +
                "uuid='" + uuid + '\'' +
                ", status='" + status + '\'' +
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
