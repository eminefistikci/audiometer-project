package com.audiometer.functional;

import java.util.List;
import java.util.stream.Collectors;

public final class ResponseProcessor {

    private ResponseProcessor() {}

    public static boolean isResponseMessage(String message) {
        return message != null && message.trim().equalsIgnoreCase("RESPONSE");
    }

    public static List<String> normalizeMessages(List<String> rawMessages) {
        return rawMessages.stream()
                .map(String::trim)
                .filter(message -> !message.isEmpty())
                .collect(Collectors.toUnmodifiableList());
    }

    public static long countResponses(List<String> rawMessages) {
        return normalizeMessages(rawMessages).stream()
                .filter(ResponseProcessor::isResponseMessage)
                .count();
    }

    public static Maybe<String> firstResponse(List<String> rawMessages) {
        return normalizeMessages(rawMessages).stream()
                .filter(ResponseProcessor::isResponseMessage)
                .findFirst()
                .map(Maybe::of)
                .orElse(Maybe.empty());
    }
}
