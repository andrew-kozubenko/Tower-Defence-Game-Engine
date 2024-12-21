package ru.nsu.t4werok.towerdefence.config.game.entities.map;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapSelectionConfig {
    private final String directoryPath = "maps"; // Директория, где хранятся карты

    /**
     * Метод для загрузки всех карт из конфигурационных файлов
     *
     * @return Список объектов MapConfig, представляющих параметры карт
     */
    public List<MapConfig> loadMaps() {
        File directory = new File(directoryPath);
        List<MapConfig> mapConfigs = new ArrayList<>();

        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Maps directory not found: " + directoryPath);
            return mapConfigs;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            ObjectMapper mapper = new ObjectMapper();
            for (File file : files) {
                try {
                    // Читаем JSON-файл и преобразуем его в объект MapConfig
                    MapConfig mapConfig = mapper.readValue(file, MapConfig.class);
                    String fileNameWithoutExtension = file.getName();
                    int dotIndex = fileNameWithoutExtension.lastIndexOf('.');
                    if (dotIndex > 0) { // Проверяем, что точка вообще существует
                        fileNameWithoutExtension = fileNameWithoutExtension.substring(0, dotIndex);
                    }
                    mapConfig.setMapName(fileNameWithoutExtension);

                    mapConfigs.add(mapConfig);
                    System.out.println("Loaded map: " + file.getName());
                } catch (IOException e) {
                    System.err.println("Failed to load map: " + file.getName());
                    e.printStackTrace();
                }
            }
        }

        return mapConfigs;
    }
}
