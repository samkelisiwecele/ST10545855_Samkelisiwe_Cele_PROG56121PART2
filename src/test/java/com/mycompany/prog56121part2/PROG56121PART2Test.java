package com.mycompany.prog56121part2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PROG56121PART2Test {

    @BeforeEach
    public void setUp() {
        // reset shared static state before every test so tests don't interfere with each other
        PROG56121PART2.sentMessageIDs.clear();
        PROG56121PART2.sentMessageHashes.clear();
        PROG56121PART2.sentRecipients.clear();
        PROG56121PART2.sentMessageTexts.clear();
        PROG56121PART2.storedMessages.clear();
        PROG56121PART2.messageCounter = 0;
    }

    // ---- checkMessageID ----

    @Test
    public void testCheckMessageID_ValidLength() {
        assertTrue(PROG56121PART2.checkMessageID("0012345678")); // 10 chars
    }

    @Test
    public void testCheckMessageID_TooLong() {
        assertFalse(PROG56121PART2.checkMessageID("00123456789")); // 11 chars
    }

    // ---- checkRecipientCell ----

    @Test
    public void testCheckRecipientCell_Success() {
        // Test Data: "+27718693002"
        String result = PROG56121PART2.checkRecipientCell("+27718693002");
        assertEquals("Cell phone number successfully captured.", result);
    }

    @Test
    public void testCheckRecipientCell_Failure() {
        // Test Data: "08575975089" (missing international code)
        String result = PROG56121PART2.checkRecipientCell("08575975089");
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", result);
    }

    // ---- checkMessageLength ----

    @Test
    public void testCheckMessageLength_Success() {
        String msg = "Hi Mike, can you join me for dinner tonight?";
        assertEquals("Message ready to send.", PROG56121PART2.checkMessageLength(msg));
    }

    @Test
    public void testCheckMessageLength_Failure() {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 260) {
            sb.append("a");
        }
        String msg = sb.toString();
        int expectedOver = msg.length() - 250;
        String result = PROG56121PART2.checkMessageLength(msg);
        assertEquals("Message exceeds 250 characters by " + expectedOver + "; please reduce the size.", result);
    }

    // ---- createMessageHash ----

    @Test
    public void testCreateMessageHash_MatchesBriefExample() {
        // Brief example: ID "0012345678", message number 0,
        // "Hi Mike, can you join me for dinner tonight?" -> "00:0:HITONIGHT"
        String hash = PROG56121PART2.createMessageHash("0012345678", 0, "Hi Mike, can you join me for dinner tonight?");
        assertEquals("00:0:HITONIGHT", hash);
    }

    @Test
    public void testCreateMessageHash_SecondExample() {
        // "Hi Keegan, did you receive the payment?" with ID "0112345678", number 1
        String hash = PROG56121PART2.createMessageHash("0112345678", 1, "Hi Keegan, did you receive the payment?");
        assertEquals("01:1:HIPAYMENT", hash);
    }

    // ---- sentMessage ----

    @Test
    public void testSentMessage_Send() {
        String result = PROG56121PART2.sentMessage(1, "0012345678", "00:0:HITONIGHT", "+27718693002", "Hi Mike, can you join me for dinner tonight?");
        assertEquals("Message successfully sent.", result);
        assertEquals(1, PROG56121PART2.returnTotalMessages());
    }

    @Test
    public void testSentMessage_Disregard() {
        String result = PROG56121PART2.sentMessage(2, "0012345678", "00:0:HITONIGHT", "+27718693002", "Hi Mike, can you join me for dinner tonight?");
        assertEquals("Press 0 to delete the message.", result);
        assertEquals(0, PROG56121PART2.returnTotalMessages());
    }

    @Test
    public void testSentMessage_Store() {
        String result = PROG56121PART2.sentMessage(3, "0012345678", "00:0:HITONIGHT", "+27718693002", "Hi Mike, can you join me for dinner tonight?");
        assertEquals("Message successfully stored.", result);
        assertEquals(1, PROG56121PART2.storedMessages.size());
        assertEquals(0, PROG56121PART2.returnTotalMessages()); // not counted as "sent"
    }

    // ---- returnTotalMessages ----

    @Test
    public void testReturnTotalMessages_OnlyCountsSent() {
        PROG56121PART2.sentMessage(1, "0012345678", "00:0:HITONIGHT", "+27718693002", "Hi Mike, can you join me for dinner tonight?");
        PROG56121PART2.sentMessage(2, "0112345678", "01:1:HIPAYMENT", "+27718693002", "Hi Keegan, did you receive the payment?");
        PROG56121PART2.sentMessage(3, "0212345678", "02:2:HITEST", "+27718693002", "Hi this is a test");

        assertEquals(1, PROG56121PART2.returnTotalMessages());
    }

    // ---- integration: full flow ----

    @Test
    public void testFullMessageFlow_SendAndPrint() {
        String id = "0012345678";
        String hash = PROG56121PART2.createMessageHash(id, PROG56121PART2.messageCounter, "Hi Mike, can you join me for dinner tonight?");
        String result = PROG56121PART2.sentMessage(1, id, hash, "+27718693002", "Hi Mike, can you join me for dinner tonight?");

        assertEquals("Message successfully sent.", result);
        assertTrue(PROG56121PART2.printMessages().contains("Hi Mike, can you join me for dinner tonight?"));
        assertTrue(PROG56121PART2.printMessages().contains(hash));
    }
}