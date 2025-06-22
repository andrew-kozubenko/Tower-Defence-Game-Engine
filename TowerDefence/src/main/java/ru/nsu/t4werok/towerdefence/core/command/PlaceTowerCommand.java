package ru.nsu.t4werok.towerdefence.core.command;

import java.util.UUID;

/**
 * Пример реальной команды: установка башни.
 * Сейчас нигде не вызывается, но готова к использованию.
 */
public record PlaceTowerCommand(
        UUID   playerId,
        String towerType,
        int    cellX,
        int    cellY
) implements GameCommand { }
