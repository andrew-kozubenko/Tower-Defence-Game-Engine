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

    /* towers */
    PLACE_TOWER,
    UPGRADE_TOWER,
    SELL_TOWER,

    /* waves / враги */
    WAVE_START,         // {idx, seed}
    ENEMY_SPAWN,        // {wave, idInWave, path}

    BASE_HP,

    PLAYER_STATS,
    PING,
    DISCONNECT
}
