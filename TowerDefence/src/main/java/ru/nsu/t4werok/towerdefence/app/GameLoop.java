package ru.nsu.t4werok.towerdefence.app;

/**
 * Чистый расчёт модели.  НЕ содержит JavaFX-кода и может выполняться
 * как на клиенте, так и на head-less-сервере.
 */
@FunctionalInterface
public interface GameLoop {
    /**
     * @param deltaSeconds  время, прошедшее с предыдущего тика, в секундах.
     */
    void tick(double deltaSeconds);
}
