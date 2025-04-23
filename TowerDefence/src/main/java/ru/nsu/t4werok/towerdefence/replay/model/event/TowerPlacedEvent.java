package ru.nsu.t4werok.towerdefence.replay.model.event;

import ru.nsu.t4werok.towerdefence.app.GameEngine;

public class TowerPlacedEvent implements ReplayEvent {
    private long timestamp;
    private String towerName;
    private int cellX, cellY;

    // нужен конструктор для Jackson
    public TowerPlacedEvent() {}

    public TowerPlacedEvent(long timestamp, String towerName, int cellX, int cellY) {
        this.timestamp = timestamp;
        this.towerName = towerName;
        this.cellX = cellX;
        this.cellY = cellY;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void apply(GameEngine engine) {
        engine.getGameController().selectTowerByName(towerName);
        engine.getGameController().placeTower(cellX, cellY);
    }
}
