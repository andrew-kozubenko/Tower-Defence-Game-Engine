package ru.nsu.t4werok.towerdefence.controller.menu;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.managers.menu.SettingsManager;

public class SettingsController {
    private final SettingsManager settingsManager;
    private final SceneController sceneController;

    public void setVolume(int volume) {
        settingsManager.setVolume(volume);
    }

    public void setResolution(String resolution) {
        settingsManager.setResolution(resolution);
    }

    public SettingsController(SceneController sceneController) {
        this.settingsManager = SettingsManager.getInstance();
        this.sceneController = sceneController;
    }

    public void initialize(Slider volumeSlider, ComboBox<String> resolutionComboBox) {
        // Установка текущих значений в UI
        volumeSlider.setValue(settingsManager.getVolume());
        resolutionComboBox.setValue(settingsManager.getResolution());

        // Слушатели изменений (если нужно обновлять в реальном времени)
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            settingsManager.setVolume(newVal.intValue());
        });

        resolutionComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            settingsManager.setResolution(newVal);
        });
    }

    public void applySettings() {
        settingsManager.applySettings();
    }

    public void onBackButtonPressed() {
        sceneController.switchTo("MainMenu");
    }
}


