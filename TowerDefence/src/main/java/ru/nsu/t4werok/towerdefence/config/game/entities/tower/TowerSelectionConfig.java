package ru.nsu.t4werok.towerdefence.config.game.entities.tower;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TowerSelectionConfig {
    private final String directoryPath = "towers"; // Директория, где хранятся карты

    /**
     * Метод для загрузки всех карт из конфигурационных файлов
     *
     * @return Список объектов TowerConfig, представляющих параметры карт
     */
    public List<TowerConfig> loadTowers() {
        File directory = new File(directoryPath);
        List<TowerConfig> towerConfigs = new ArrayList<>();

        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Tower directory not found: " + directoryPath);
            return towerConfigs;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            ObjectMapper mapper = new ObjectMapper();
            for (File file : files) {
                try {
                    // Читаем JSON-файл и преобразуем его в объект TowerConfig
                    TowerConfig towerConfig = mapper.readValue(file, TowerConfig.class);
                    towerConfigs.add(towerConfig);
                    System.out.println("Loaded tower: " + file.getName());
                } catch (IOException e) {
                    System.err.println("Failed to load tower: " + file.getName());
                    e.printStackTrace();
                }
            }
        }

        return towerConfigs;
    }
}
