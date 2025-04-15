package ru.nsu.t4werok.towerdefence.config.menu;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Класс для чтения/записи JSON-файла с настройками
 */
public class SettingsSelectionConfig {
    // Путь к папке вида: <папка_пользователя>/Documents/Games/TowerDefenceSD/settings
    private final Path settingsDirectoryPath;
    // Путь к файлу настроек settings.json
    private final Path settingsFilePath;

    private SettingsConfig settingsConfig;

    public SettingsSelectionConfig() {
        this.settingsDirectoryPath = Paths.get(
                System.getProperty("user.home"),
                "Documents",
                "Games",
                "TowerDefenceSD",
                "settings"
        );
        try {
            // Создаём директорию (и все недостающие) при отсутствии
            Files.createDirectories(settingsDirectoryPath);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для настроек: " + settingsDirectoryPath, e);
        }

        // Определяем путь к файлу с настройками
        this.settingsFilePath = settingsDirectoryPath.resolve("settings.json");
    }

    /**
     * Метод для загрузки настроек из файла. Если файл отсутствует,
     * создаёт объект настроек по умолчанию.
     */
    public SettingsConfig loadSettings() {
        ObjectMapper mapper = new ObjectMapper();
        File file = settingsFilePath.toFile();

        if (file.exists()) {
            try {
                settingsConfig = mapper.readValue(file, SettingsConfig.class);
            } catch (IOException e) {
                System.err.println("Error loading settings: " + e.getMessage());
                // Если загрузка не удалась, создаём новый объект с настройками по умолчанию
                settingsConfig = new SettingsConfig();
            }
        } else {
            System.out.println("Settings file not found. Using default settings.");
            // Если файла настроек нет, создаём новый объект с настройками по умолчанию
            settingsConfig = new SettingsConfig();
        }

        return settingsConfig;
    }

    /**
     * Метод для сохранения текущих настроек в файл
     */
    public void applySettings() {
        if (settingsConfig == null) {
            // Если почему-то нет текущих настроек, ничего не делаем или можно создать новые
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        File file = settingsFilePath.toFile();

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, settingsConfig);
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }
}
