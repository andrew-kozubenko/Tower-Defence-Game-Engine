package ru.nsu.t4werok.towerdefence.model.game.entities.map;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private final Integer width; // Ширина карты
    private final Integer height; // Высота карты
    private final List<List<Integer[]>> enemyPaths; // Пути врагов: список списков координат
    private final List<Integer[]> towerPositions; // Доступные позиции для башен
    private final Integer[] spawnPoint; // Точка появления врагов
    private Base base; // База

    public GameMap(Integer width, Integer height, List<List<Integer[]>> enemyPaths,
                   List<Integer[]> towerPositions, Integer[] spawnPoint, Base base) {
        this.width = width;
        this.height = height;
        this.enemyPaths = enemyPaths;
        this.towerPositions = towerPositions;
        this.spawnPoint = spawnPoint;
        this.base = base;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public List<List<Integer[]>> getEnemyPaths() {
        return enemyPaths;
    }

    public List<Integer[]> getTowerPositions() {
        return towerPositions;
    }

    public Integer[] getSpawnPoint() {
        return spawnPoint;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    // Проверка, находится ли точка в пределах карты
    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
