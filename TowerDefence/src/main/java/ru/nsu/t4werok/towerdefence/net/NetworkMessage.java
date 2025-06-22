/* src/main/java/ru/nsu/t4werok/towerdefence/net/NetworkMessage.java */
package ru.nsu.t4werok.towerdefence.net;

/**
 * Простейший текстовый протокол:
 * PLACE_TOWER;{towerName};{x};{y}
 */
public final class NetworkMessage {
    private static final String SEP = ";";
    private static final String PLACE_TOWER = "PLACE_TOWER";

    private NetworkMessage() {}

    public static String encodePlaceTower(String towerName, int x, int y) {
        return PLACE_TOWER + SEP + towerName + SEP + x + SEP + y;
    }

    public static boolean isPlaceTower(String raw) {
        return raw != null && raw.startsWith(PLACE_TOWER);
    }

    public static String[] split(String raw) {
        return raw.split(SEP);
    }
}
