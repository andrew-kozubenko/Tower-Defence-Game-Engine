package ru.nsu.t4werok.towerdefence.config.game.entities.tower;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.nsu.t4werok.towerdefence.utils.ResourceManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TowerSelectionConfig {

    // Путь к папке вида: <папка_пользователя>/Documents/Games/TowerDefenceSD/towers
    private final Path towersDirectoryPath = ResourceManager.getTowersDir();

    public TowerSelectionConfig() {
        try {
            Files.createDirectories(towersDirectoryPath);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для башен: " + towersDirectoryPath, e);
        }
    }


    /**
     * Метод для загрузки всех конфигураций башен из JSON-файлов
     *
     * @return Список объектов TowerConfig, представляющих параметры башен
     */
    public List<TowerConfig> loadTowers() {
        List<TowerConfig> towerConfigs = new ArrayList<>();
        File directory = towersDirectoryPath.toFile();

        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Towers directory not found: " + towersDirectoryPath);
            return towerConfigs;
        }

        // Получаем все .json-файлы из папки
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            ObjectMapper mapper = new ObjectMapper();
            for (File file : files) {
                try {
                    // Читаем JSON-файл и преобразуем его в TowerConfig
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
