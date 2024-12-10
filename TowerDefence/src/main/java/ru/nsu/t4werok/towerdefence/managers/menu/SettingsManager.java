package ru.nsu.t4werok.towerdefence.managers.menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class SettingsManager {
    private static final String SETTINGS_FILE = "settings.json";
    private static SettingsManager instance = null;

    private int volume = 50; // Громкость по умолчанию (от 0 до 100)
    private String resolution = "800x600"; // Разрешение по умолчанию

    private Stage mainStage; // Ссылка на главное окно приложения

    private SettingsManager() {}

    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setMainStage(Stage stage) {
        this.mainStage = stage;
        applyResolution(); // Устанавливаем начальное разрешение при запуске
    }

    private void applyResolution() {
        if (mainStage != null && resolution.matches("\\d+x\\d+")) {
            String[] dimensions = resolution.split("x");
            double width = Double.parseDouble(dimensions[0]);
            double height = Double.parseDouble(dimensions[1]);
            mainStage.setWidth(width);
            mainStage.setHeight(height);
        }
    }

    private void loadSettings() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(SETTINGS_FILE);

        if (file.exists()) {
            try {
                SettingsManager loadedSettings = mapper.readValue(file, SettingsManager.class);
                this.volume = loadedSettings.volume;
                this.resolution = loadedSettings.resolution;
            } catch (IOException e) {
                System.err.println("Error loading settings: " + e.getMessage());
                // If loading fails, use defaults
            }
        } else {
            System.out.println("Settings file not found. Using default settings.");
        }
    }

    public void applySettings() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(SETTINGS_FILE);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, this);
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }

        applyResolution();
    }
}
