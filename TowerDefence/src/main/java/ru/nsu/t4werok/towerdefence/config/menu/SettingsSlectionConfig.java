package ru.nsu.t4werok.towerdefence.config.menu;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class SettingsSlectionConfig {
    private static final String SETTINGS_FILE = "settings/settings.json";
    private SettingsConfig settingsConfig; // Убираем прямую инициализацию для контроля

    public SettingsConfig loadSettings() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(SETTINGS_FILE);

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

    public void applySettings() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(SETTINGS_FILE);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, settingsConfig);
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }
}
