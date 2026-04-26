package com.example.gps.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class GpsMessageSerde {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private GpsMessageSerde() {
    }

    public static String toJson(GpsMessage message) {
        try {
            return OBJECT_MAPPER.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize GPS message", e);
        }
    }

    public static GpsMessage fromJson(String raw) {
        try {
            return OBJECT_MAPPER.readValue(raw, GpsMessage.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to deserialize GPS message: " + raw, e);
        }
    }
}
