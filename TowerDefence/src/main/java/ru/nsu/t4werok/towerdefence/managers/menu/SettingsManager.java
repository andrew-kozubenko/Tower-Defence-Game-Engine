package ru.nsu.t4werok.towerdefence.managers.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.config.menu.SettingsConfig;
import ru.nsu.t4werok.towerdefence.config.menu.SettingsSlectionConfig;

import java.io.File;
import java.io.IOException;

public class SettingsManager {
    private static SettingsManager instance = null;
    private SettingsConfig settingsConfig;
    private final SettingsSlectionConfig settingsSlectionConfig;

    private Stage mainStage; // Ссылка на главное окно приложения

    public SettingsManager() {
        this.settingsConfig = new SettingsConfig();
        this.settingsSlectionConfig = new SettingsSlectionConfig();
        loadSettings();
    }

    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    public int getVolume() {
        return settingsConfig.getVolume();
    }

    public void setVolume(int volume) {
        settingsConfig.setVolume(volume);
    }

    public String getResolution() {
        return settingsConfig.getResolution();
    }

    public void setResolution(String resolution) {
        settingsConfig.setResolution(resolution);
    }

    public void setMainStage(Stage stage) {
        this.mainStage = stage;
        applySettings(); // Устанавливаем начальное разрешение при запуске
    }

    private void applyResolution() {
        String resolution = settingsConfig.getResolution();
        if (mainStage != null && resolution.matches("\\d+x\\d+")) {
            String[] dimensions = resolution.split("x");
            double width = Double.parseDouble(dimensions[0]);
            double height = Double.parseDouble(dimensions[1]);
            mainStage.setWidth(width);
            mainStage.setHeight(height);

            // Центрируем окно
            mainStage.centerOnScreen();
        }
    }

    private void loadSettings() {
        this.settingsConfig = settingsSlectionConfig.loadSettings();
    }

    public void applySettings() {
        settingsSlectionConfig.applySettings();
        applyResolution();
    }
}
