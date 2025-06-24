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
    PLACE_TOWER,      // сигнал для отображения башни у всех игроков
    UPGRADE_TOWER,    // сигнал для улучшения башни
    SELL_TOWER,       // сигнал для продажи башни
    WAVE_SPAWN,
    PLAYER_STATS,
    PING,
    DISCONNECT
}
