package ru.nsu.t4werok.towerdefence.net.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Универсальное сетевое сообщение.
 *
 * Вместо сериализации самого POJO сериализуем примитивную Map:
 *   {"type":"HELLO","payload":{"name":"Nick"}}
 * Так Jackson не «трогает» внутренности модуля и не пытается
 * рефлексивно лезть в приватные поля / конструкторы.
 */
public final class NetMessage {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final NetMessageType type;
    private final Map<String, Object> payload;

    /* ------------------- ctors ------------------- */

    /** Создание «вручную» из кода */
    public NetMessage(NetMessageType type, Map<String, Object> payload) {
        this.type    = type;
        this.payload = payload == null ? new HashMap<>() : new HashMap<>(payload);
    }

    /** Пакет-приватный: используется только из fromJson() */
    NetMessage(NetMessageType type) {
        this(type, new HashMap<>());
    }

    /* ------------------ getters ------------------ */

    public NetMessageType getType() { return type; }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) { return (T) payload.get(key); }

    /* ---------------- serialization ------------- */

    public String toJson() {
        try {
            Map<String, Object> wrapper = Map.of(
                    "type",    type.name(),
                    "payload", payload
            );
            return MAPPER.writeValueAsString(wrapper);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize NetMessage", e);
        }
    }

    public static NetMessage fromJson(String json) {
        try {
            @SuppressWarnings("unchecked")
            Map<String,Object> wrapper =
                    MAPPER.readValue(json, HashMap.class);

            NetMessageType type = NetMessageType.valueOf((String) wrapper.get("type"));
            @SuppressWarnings("unchecked")
            Map<String,Object> payload =
                    (Map<String,Object>) wrapper.getOrDefault("payload", new HashMap<>());

            return new NetMessage(type, payload);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse NetMessage: " + json, e);
        }
    }

    /* --------------- fluent helpers ------------- */

    public static NetMessage of(NetMessageType t) { return new NetMessage(t); }

    public NetMessage with(String k, Object v) { payload.put(k, v); return this; }
}
