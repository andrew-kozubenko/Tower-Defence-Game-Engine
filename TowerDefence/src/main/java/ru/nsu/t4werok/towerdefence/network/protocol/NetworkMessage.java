package ru.nsu.t4werok.towerdefence.network.protocol;

public class NetworkMessage {
    private MessageType type;
    private String payload; // JSON или сериализованный объект

    public NetworkMessage(MessageType type, String payload) {
        this.type = type;
        this.payload = payload;
    }

    public MessageType getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }
}