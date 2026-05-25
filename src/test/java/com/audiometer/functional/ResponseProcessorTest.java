package com.audiometer.functional;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResponseProcessorTest {

    @Test
    void shouldNormalizeMessages() {

        List<String> raw = List.of(
                " RESPONSE ",
                "",
                "RESPONSE",
                " test "
        );

        List<String> normalized =
                ResponseProcessor.normalizeMessages(raw);

        assertEquals(3, normalized.size());
        assertEquals("RESPONSE", normalized.get(0));
    }

    @Test
    void shouldCountResponsesCorrectly() {

        List<String> raw = List.of(
                "RESPONSE",
                "NOISE",
                "RESPONSE",
                "OTHER"
        );

        long count = ResponseProcessor.countResponses(raw);

        assertEquals(2, count);
    }

    @Test
    void shouldReturnFirstResponse() {

        List<String> raw = List.of(
                "NOISE",
                "RESPONSE",
                "RESPONSE"
        );

        Maybe<String> response =
                ResponseProcessor.firstResponse(raw);

        assertTrue(response.isPresent());
        assertEquals("RESPONSE", response.get());
    }

    @Test
    void shouldReturnEmptyMaybeWhenNoResponseExists() {

        List<String> raw = List.of(
                "NOISE",
                "OTHER"
        );

        Maybe<String> response =
                ResponseProcessor.firstResponse(raw);

        assertFalse(response.isPresent());
    }
}
