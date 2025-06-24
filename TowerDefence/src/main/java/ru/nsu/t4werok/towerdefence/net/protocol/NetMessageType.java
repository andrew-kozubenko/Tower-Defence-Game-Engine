package ru.nsu.t4werok.towerdefence.net.protocol;

/**
 * Типы сетевых сообщений.
 * START теперь содержит:
 *   { map : "maps/Forest.json",
 *     data: "<base64-zip-blob со всеми ресурсами>" }
 */
public enum NetMessageType {
    /* lobby / handshake */
    HELLO, PLAYERS, READY, CANCEL_READY, START, DISCONNECT,

    /* башни */
    PLACE_TOWER, UPGRADE_TOWER, SELL_TOWER,

    /* волны (новые) */
    WAVE_REQ,      // клиент → хост: «запусти следующую волну»
    WAVE_SYNC,     // хост   → все : {idx,seed} – объявление начала волны
    STATE_SYNC,    // хост   → все : полный снимок врагов + HP базы

    /* прочее */
    PING,
    PLAYER_STATS
}
