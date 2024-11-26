package ru.nsu.t4werok.towerdefence.managers.menu;

public class SettingsManager {
    private static SettingsManager instance = null;

    private int volume = 50; // Громкость по умолчанию (от 0 до 100)
    private String resolution = "1920x1080"; // Разрешение по умолчанию

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

    public void applySettings() {
        // Здесь можно добавить логику, например, обновление UI или запись в файл
        System.out.println("Settings applied: Volume = " + volume + ", Resolution = " + resolution);
    }
}
