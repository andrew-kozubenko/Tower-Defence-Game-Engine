package ru.nsu.t4werok.towerdefence.config.game.entities.map;

import ru.nsu.t4werok.towerdefence.model.game.entities.map.Base;

import java.util.List;

public class MapConfig {
    private String mapName;
    private Integer width; // Ширина карты
    private Integer height; // Высота карты
    private List<List<Integer[]>> enemyPaths; // Пути врагов: список списков координат
    private List<Integer[]> towerPositions; // Доступные позиции для башен
    private Integer[] spawnPoint; // Точка появления врагов
    private Base base; // База
    private String backgroundImagePath;
    private String baseImagePath;
    private String towerImagePath;
    private String spawnPointImagePath;

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public List<List<Integer[]>> getEnemyPaths() {
        return enemyPaths;
    }

    public void setEnemyPaths(List<List<Integer[]>> enemyPaths) {
        this.enemyPaths = enemyPaths;
    }

    public List<Integer[]> getTowerPositions() {
        return towerPositions;
    }

    public void setTowerPositions(List<Integer[]> towerPositions) {
        this.towerPositions = towerPositions;
    }

    public Integer[] getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Integer[] spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public String getBackgroundImagePath() {
        return backgroundImagePath;
    }

    public void setBackgroundImagePath (String backgroundImage) {
        this.backgroundImagePath = backgroundImage;
    }

    public String getBaseImagePath() {
        return baseImagePath;
    }

    public void setBaseImagePath(String baseImagePath) {
        this.baseImagePath = baseImagePath;
    }

    public String getTowerImagePath() {
        return towerImagePath;
    }

    public void setTowerImagePath(String towerImagePath) {
        this.towerImagePath = towerImagePath;
    }

    public String getSpawnPointImagePath() {
        return spawnPointImagePath;
    }

    public void setSpawnPointImagePath(String spawnPointImagePath) {
        this.spawnPointImagePath = spawnPointImagePath;
    }
}
