package com.xebia.payment.v2.domain;

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
    @OneToOne(optional = true)
    @Cascade(value = {org.hibernate.annotations.CascadeType.MERGE})
    private Payment payment;
    @Column(columnDefinition = "clob")
    @Lob
    private String document;

    public Clerk() {
    }

    public Clerk(UUID uuid, int status) {
        this.uuid = uuid;
        this.status = status;
        this.document = "{\"uuid\":\""+uuid+"\",\"status\":"+status+"}";
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
        JsonNode paymentNode = jsonDocument.get("payment");
        if (paymentNode != null && paymentNode.asText() != "null") {
            this.setPayment(new Payment(paymentNode));
        }
    }

    public int getStatus() {
        return status;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setPayment(Payment payment) throws Exception {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getDocument() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode parsedDocument = (ObjectNode) mapper.readValue(document, JsonNode.class);
        parsedDocument.replace("payment", this.payment.asJson());
        this.document = mapper.writeValueAsString(parsedDocument);
        return document;
    }

    @Override
    public String toString() {
        return "Clerk{" +
                "uuid='" + uuid + '\'' +
                ", status='" + status + '\'' + "'}";
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
