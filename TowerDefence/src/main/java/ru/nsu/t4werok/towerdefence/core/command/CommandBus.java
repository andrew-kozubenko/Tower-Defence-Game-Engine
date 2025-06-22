package ru.nsu.t4werok.towerdefence.core.command;

/** Транспорт для команд. Позже вместо «локального» появится сетевой. */
public interface CommandBus {
    void send(GameCommand command);
}
