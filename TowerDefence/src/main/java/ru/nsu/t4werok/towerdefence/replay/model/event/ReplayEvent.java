package ru.nsu.t4werok.towerdefence.replay.model.event;

import ru.nsu.t4werok.towerdefence.app.GameEngine;

public interface ReplayEvent {
    /** Время (в миллисекундах) от старта записи */
    long getTimestamp();

    /** Применить это событие к игровому движку */
    void apply(GameEngine engine);
}
