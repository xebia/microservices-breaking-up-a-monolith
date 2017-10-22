package com.xebia.payment.v2.domain;

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
    public static final int SHOPPING = 0;
    public static final int PAYING = 0;
    public static final int FULFILLING = 0;

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
        // Empty constructor required by framework
    }

    public Clerk(UUID uuid, int status) {
        this.uuid = uuid;
        this.status = status;
        this.document = "{\"uuid\":\"" + uuid + "\",\"status\":" + status + "}";
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

    private void loadFromString(String document) throws Exception {
        try {
            this.document = document;
            JsonNode jsonDocument = mapper.readValue(document, JsonNode.class);
            this.uuid = UUID.fromString(jsonDocument.get("uuid").asText());
            this.status = Integer.parseInt(jsonDocument.get("status").asText());
            JsonNode paymentNode = jsonDocument.get("payment");
            if (paymentNode != null && !"null".equals(paymentNode.asText())) {
                this.setPayment(new Payment(paymentNode));
            }
        } catch (IOException e) {
            this.document = "{}";
            LOG.error("Error '" + e.getMessage() + "' reading document " + document);
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
