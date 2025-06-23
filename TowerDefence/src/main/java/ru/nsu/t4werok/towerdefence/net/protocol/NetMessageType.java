package ru.nsu.t4werok.towerdefence.net.protocol;

/**
 * Перечень всех сетевых сообщений.
 * Расширен для полноценного кооператива.
 */
public enum NetMessageType {
    HELLO,            // hand-shake
    PLAYERS,          // список игроков/их статусы
    READY,
    CANCEL_READY,
    START,            // запуск игры (map + seed)
    PLACE_TOWER,
    UPGRADE_TOWER,
    SELL_TOWER,
    WAVE_SPAWN,
    PLAYER_STATS,
    PING,
    DISCONNECT
}
