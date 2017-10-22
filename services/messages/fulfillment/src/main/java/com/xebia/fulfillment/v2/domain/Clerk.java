package com.xebia.fulfillment.v2.domain;

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
    public static final int FULFILLING = 0;

    private transient ObjectMapper mapper = new ObjectMapper();

    @Id
    private UUID uuid;
    private int status = FULFILLING;
    @OneToOne(optional = true)
    @Cascade(value = {org.hibernate.annotations.CascadeType.MERGE})
    private Shipment shipment;
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

    private void loadFromString(String document) {
        try {
            this.document = document;
            JsonNode jsonDocument = mapper.readValue(document, JsonNode.class);
            this.uuid = UUID.fromString(jsonDocument.get("uuid").asText());
            this.status = Integer.parseInt(jsonDocument.get("status").asText());
            JsonNode shipmentNode = jsonDocument.get("shipment");
            if (shipmentNode != null && !"null".equals(shipmentNode.asText())) {
                this.setShipment(new Shipment(shipmentNode));
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

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getDocument() {
        try {
            ObjectNode parsedDocument = (ObjectNode) mapper.readValue(document, JsonNode.class);
            parsedDocument.replace("shipment", this.shipment.asJson());
            document = mapper.writeValueAsString(parsedDocument);
        } catch (IOException e) {
            document = "{}";
            LOG.error("Error '" + e.getMessage() + "' reading document " + document);
        }
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
