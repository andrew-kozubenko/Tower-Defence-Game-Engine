package ru.nsu.t4werok.towerdefence.managers.menu;


import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.config.menu.SettingsConfig;
import ru.nsu.t4werok.towerdefence.config.menu.SettingsSelectionConfig;


public class SettingsManager {
    private static SettingsManager instance = null;
    private SettingsConfig settingsConfig;
    private final SettingsSelectionConfig settingsSlectionConfig;
    private boolean isRunningGame = false;
    private double width = 800;
    private double height = 600;

    public boolean isRunningGame() {
        return isRunningGame;
    }

    public void setRunningGame(boolean isRunningGame) {
        this.isRunningGame = isRunningGame;
    }

    private Stage mainStage; // Ссылка на главное окно приложения

    public SettingsManager() {
        this.settingsConfig = new SettingsConfig();
        this.settingsSlectionConfig = new SettingsSelectionConfig();
        loadSettings();
    }

    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
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
//        applySettings(); // Устанавливаем начальное разрешение при запуске
    }

    private void applyResolution() {
        String resolution = settingsConfig.getResolution();
        if (mainStage != null && resolution.matches("\\d+x\\d+")) {
            String[] dimensions = resolution.split("x");

//            this.width = Double.parseDouble(dimensions[0]);
//            this.height = Double.parseDouble(dimensions[1]);

            double width = Double.parseDouble(dimensions[0]);
            double height = Double.parseDouble(dimensions[1]);

            mainStage.setWidth(width);
            mainStage.setHeight(height);

            mainStage.setMinWidth(width);
            mainStage.setMinHeight(height);
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
