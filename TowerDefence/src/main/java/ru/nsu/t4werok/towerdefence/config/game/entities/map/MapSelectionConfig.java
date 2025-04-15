package ru.nsu.t4werok.towerdefence.config.game.entities.map;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MapSelectionConfig {
    // Папка, где будут храниться файлы карт.
    // Формируется в директории вида: <папка_пользователя>/Documents/Games/TowerDefenceSD/maps
    private final Path mapsDirectoryPath;

    // В конструкторе проверяем, что нужные директории существуют, иначе создаём
    public MapSelectionConfig() {
        this.mapsDirectoryPath = Paths.get(
                System.getProperty("user.home"),
                "Documents",
                "Games",
                "TowerDefenceSD",
                "maps"
        );
        try {
            Files.createDirectories(mapsDirectoryPath);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Не удалось создать директорию для карт: " + mapsDirectoryPath, e
            );
        }
    }

    /**
     * Метод для загрузки всех карт из конфигурационных файлов
     *
     * @return Список объектов MapConfig, представляющих параметры карт
     */
    public List<MapConfig> loadMaps() {
        List<MapConfig> mapConfigs = new ArrayList<>();

        // Получаем объект File на основе Path
        File directory = mapsDirectoryPath.toFile();
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Maps directory not found: " + mapsDirectoryPath);
            return mapConfigs;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            ObjectMapper mapper = new ObjectMapper();
            for (File file : files) {
                try {
                    // Читаем JSON-файл и преобразуем его в объект MapConfig
                    MapConfig mapConfig = mapper.readValue(file, MapConfig.class);

                    // Определяем название карты, убирая у файла расширение
                    String fileNameWithoutExtension = file.getName();
                    int dotIndex = fileNameWithoutExtension.lastIndexOf('.');
                    if (dotIndex > 0) { // Проверяем, что точка действительно есть
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
