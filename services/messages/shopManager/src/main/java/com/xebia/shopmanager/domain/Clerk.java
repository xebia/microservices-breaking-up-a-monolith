package com.xebia.shopmanager.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hibernate.annotations.Cascade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Entity
public class Clerk {
    private static final Logger LOG = LoggerFactory.getLogger(Clerk.class);

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
        // Empty constructor required by framework
    }

    public Clerk(WebUser webUser, UUID uuid) throws Exception {
        this.webUser = webUser;
        this.uuid = uuid;
        this.document = "{\"uuid\":\"" + uuid + "\",\"status\":" + status + "}";
        getDocument();
    }

    public Clerk(WebUser webUser) throws Exception {
        this.webUser = webUser;
        this.uuid = UUID.randomUUID();
        this.document = "{\"uuid\":\"" + uuid + "\",\"status\":" + status + "}";
        getDocument();
    }

    public Clerk(String document) throws Exception {
        loadFromString(document);
    }

    public Clerk(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();
            loadFromString(new String(data));
        } catch (Exception e) {
            LOG.error("Error reading file " + file.getAbsolutePath() + ": " + e.getMessage());
        }
    }

    private void loadFromString(String document) {
        try {
            this.document = document;
            JsonNode jsonDocument = mapper.readValue(document, JsonNode.class);
            this.uuid = UUID.fromString(jsonDocument.get("uuid").asText());
            this.status = Integer.parseInt(jsonDocument.get("status").asText());
            JsonNode webUserNode = jsonDocument.get("webUser");
            if (webUserNode != null && !"null".equals(webUserNode.asText())) {
                this.setWebUser(new WebUser(webUserNode));
            }
        } catch (IOException e) {
            this.document = "{}";
            LOG.error("Error '" + e.getMessage() + "' reading document " + document);
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Clerk clerk = (Clerk) o;
        return uuid.equals(clerk.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

}
