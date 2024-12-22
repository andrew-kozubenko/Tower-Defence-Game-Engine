package ru.nsu.t4werok.towerdefence.config.menu;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class SettingsConfig {
    private int volume = 50; // Громкость по умолчанию (от 0 до 100)
    private String resolution = "800x600"; // Разрешение по умолчанию

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
}
