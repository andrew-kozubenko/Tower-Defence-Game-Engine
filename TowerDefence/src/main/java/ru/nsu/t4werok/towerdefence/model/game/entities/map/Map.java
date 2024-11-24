package ru.nsu.t4werok.towerdefence.model.game.entities.map;

import java.util.ArrayList;
import java.util.List;

public class Map {
    private final int width; // Ширина карты
    private final int height; // Высота карты
    private final List<List<Integer[]>> enemyPaths; // Пути врагов: список списков координат
    private final List<Integer[]> towerPositions; // Доступные позиции для башен
    private final Integer[] spawnPoint; // Точка появления врагов
    private Base base; // База

    public Map(Integer width, Integer height, Integer[] spawnPoint,
               Integer xBase, Integer yBase, Integer healthBase) {
        this.width = width;
        this.height = height;
        this.enemyPaths = new ArrayList<>();
        this.towerPositions = new ArrayList<>();
        this.spawnPoint = spawnPoint;
        this.base = new Base(xBase, yBase, healthBase);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
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

    public void addEnemyPath(List<Integer[]> path) {
        enemyPaths.add(path);
    }

    public void addTowerPosition(Integer[] position) {
        towerPositions.add(position);
    }

    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public static Map loadFromConfig(String filePath) {

        return null;
    }
}
