package com.xebia.payment.v2.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Entity
public class Document {
    private transient ObjectMapper mapper = new ObjectMapper();
    private transient JsonNode document;
    private transient Clerk clerk;

    @Id
    private UUID uuid;
    private UUID clerkUuid;
    @Column(columnDefinition="clob")
    @Lob
    private String documentAsString;

    public Document() {
    }

    public Document(String documentAsString) throws IOException {
        loadFromString(documentAsString);
    }

    public void loadFromString(String documentAsString) throws IOException {
        document = mapper.readValue(documentAsString, JsonNode.class);
        this.documentAsString = documentAsString;
        this.clerk = new Clerk(document);
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        clerkUuid = getClerk().getUuid();
    }

    public Document(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[fis.available()];
        fis.read(data);
        fis.close();
        loadFromString(new String(data));
    }

    public Clerk getClerk() {
        return this.clerk;
    }

    public void setClerk(Clerk clerk) {
        this.clerk = clerk;
        this.clerkUuid = clerk.getUuid();
    }

    public UUID getClerkUuid() {
        return clerkUuid;
    }

    public void setClerkUuid(UUID clerkUuid) {
        this.clerkUuid = clerkUuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    protected ObjectNode reAssembleDocument() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = (ObjectNode)mapper.readValue(documentAsString, JsonNode.class);
        node.replace("payment", clerk.getPayment().asJson());
        return node;
    }

    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String valueAsString = "";
        try {
            valueAsString = mapper.writeValueAsString(reAssembleDocument());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valueAsString;
    }

}
