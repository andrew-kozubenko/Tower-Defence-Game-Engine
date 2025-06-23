package ru.nsu.t4werok.towerdefence.net.protocol;

/**
 * Типы сетевых сообщений.
 * START теперь содержит:
 *   { map : "maps/Forest.json",
 *     data: "<base64-zip-blob со всеми ресурсами>" }
 */
public enum NetMessageType {
    HELLO,            // hand-shake
    PLAYERS,          // список игроков/их статусы
    READY,
    CANCEL_READY,
    START,            // запуск игры + zip-blob ресурсов
    PLACE_TOWER,
    UPGRADE_TOWER,
    SELL_TOWER,
    WAVE_SPAWN,
    PLAYER_STATS,
    PING,
    DISCONNECT
}
