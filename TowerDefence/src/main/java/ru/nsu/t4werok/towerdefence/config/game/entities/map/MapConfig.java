package ru.nsu.t4werok.towerdefence.config.game.entities.map;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.Base;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MapConfig {
    private Integer width; // Ширина карты
    private Integer height; // Высота карты
    private List<List<Integer[]>> enemyPaths; // Пути врагов: список списков координат
    private List<Integer[]> towerPositions; // Доступные позиции для башен
    private Integer[] spawnPoint; // Точка появления врагов
    private Base base; // База

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

    public void loadFromJson(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        MapConfig loadedConfig = mapper.readValue(new File(filePath), MapConfig.class);
        this.width = loadedConfig.getWidth();
        this.height = loadedConfig.getHeight();
        this.enemyPaths = loadedConfig.getEnemyPaths();
        this.towerPositions = loadedConfig.getTowerPositions();
        this.spawnPoint = loadedConfig.getSpawnPoint();
        this.base = loadedConfig.getBase();
    }

    public static void saveToJson(MapConfig config, String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), config);
    }

    public void setMapName(String fileNameWithoutExtension) {
    }
}
