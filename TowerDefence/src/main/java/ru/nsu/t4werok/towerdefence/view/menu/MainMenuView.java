package ru.nsu.t4werok.towerdefence.view.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ru.nsu.t4werok.towerdefence.controller.menu.MainMenuController;
import ru.nsu.t4werok.towerdefence.managers.menu.SettingsManager;

public class MainMenuView {
    private final Scene scene;
    private final SettingsManager settingsManager;

    public MainMenuView(MainMenuController controller) {
        this.settingsManager = SettingsManager.getInstance(); // Получаем менеджер настроек
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);

        Label settingsLabel = new Label(); // Метка для отображения текущих настроек
        updateSettingsLabel(settingsLabel); // Устанавливаем начальное значение

        Button playButton = new Button("Play");
        playButton.setOnAction(e -> controller.onPlayButtonPressed());

        Button replayButton = new Button("Replays");
        replayButton.setOnAction(e -> controller.onReplayButtonPressed());

        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(e -> {
            controller.onSettingsButtonPressed();
            updateSettingsLabel(settingsLabel); // Обновляем настройки после возврата
        });

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> controller.onExitButtonPressed());

        layout.getChildren().addAll(settingsLabel, playButton, replayButton, settingsButton, exitButton);
        this.scene = new Scene(layout, 400, 300);
    }

    public Scene getScene() {
        return scene;
    }

    private void updateSettingsLabel(Label settingsLabel) {
        // Обновляем текст метки с текущими настройками
        settingsLabel.setText(String.format("Volume: %d, Resolution: %s",
                settingsManager.getVolume(),
                settingsManager.getResolution()));
    }
}
