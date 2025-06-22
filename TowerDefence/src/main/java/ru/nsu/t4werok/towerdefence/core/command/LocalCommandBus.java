package ru.nsu.t4werok.towerdefence.core.command;

import java.util.function.Consumer;

/**
 * Простейшая реализация: команды сразу
 * передаются в обработчик без сериализации.
 */
public class LocalCommandBus implements CommandBus {

    private final Consumer<GameCommand> handler;

    public LocalCommandBus(Consumer<GameCommand> handler) {
        this.handler = handler;
    }

    @Override
    public void send(GameCommand command) {
        handler.accept(command);
    }
}
