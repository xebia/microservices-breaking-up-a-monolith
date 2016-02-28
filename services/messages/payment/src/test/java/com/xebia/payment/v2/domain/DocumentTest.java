package com.xebia.payment.v2.domain;

import com.xebia.payment.v2.PaymentApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PaymentApplication.class)
@WebAppConfiguration
public class DocumentTest {

    @Test
    public void testDocumentCanBeParsedFromJSON() throws IOException {
        String doc = "{\"uuid\":\"cb62a983-ffb5-471e-9547-71d6d46a6440\",\"status\":0,\"payment\":{\"uuid\":\"3866bb81-5ef3-431a-9c2b-bfabc9ccfad9\",\"datePaid\":1456252444,\"total\":0.0,\"cardId\":\"c123\",\"description\":null}}";
        Document document = new Document(doc);
        assertEquals("cb62a983-ffb5-471e-9547-71d6d46a6440", document.getClerk().getUuid().toString());
        assertEquals(new Date(1456252444), document.getClerk().getPayment().getDatePaid());
    }

    @Test
    public void testExtraDataRemainsUnchangedAfterParse() throws Exception {
        File file = new File(getClass().getClassLoader().getResource("clerk.json").getFile());
        Document document = new Document(file);
        assertEquals("a89a65ae-6d1a-42e5-a8f1-11b6d190286e", document.getClerk().getUuid().toString());
        String newData = document.toString();
        assertTrue(newData.indexOf("\"uuid\":\"" + document.getClerk().getUuid() + "\",") > 0);
        assertTrue(newData.indexOf("\"cardId\":\"" + document.getClerk().getPayment().getCardId() + "\",") > 0);
        assertTrue(newData.indexOf("\"price\":51.420808805416065") > 0);
    }
}
