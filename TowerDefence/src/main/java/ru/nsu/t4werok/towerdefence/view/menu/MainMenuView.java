package ru.nsu.t4werok.towerdefence.view.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.config.menu.SettingsConfig;
import ru.nsu.t4werok.towerdefence.controller.menu.MainMenuController;
import ru.nsu.t4werok.towerdefence.managers.menu.SettingsManager;

public class MainMenuView {
    private Scene scene;
    private final SettingsManager settingsManager;
    private final VBox layout; // Сохраняем ссылку на layout для обновлений

    public MainMenuView(MainMenuController controller) {
        this.settingsManager = SettingsManager.getInstance(); // Получаем менеджер настроек
        layout = new VBox(15); // Контейнер для кнопок
        layout.setAlignment(Pos.CENTER); // Устанавливаем начальное выравнивание
//        layout.setStyle("-fx-padding: 20;"); // Дополнительное пространство вокруг

        Label settingsLabel = new Label(); // Метка для отображения текущих настроек
        updateSettingsLabel(settingsLabel); // Устанавливаем начальное значение

        // Создание кнопок
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

        // Добавляем кнопки в layout
        layout.getChildren().addAll(settingsLabel, playButton, replayButton, settingsButton, exitButton);

        // Создаём сцену
        this.scene = new Scene(layout, 400, 300); // Указываем стартовые размеры

        layout.prefWidthProperty().bind(this.scene.widthProperty());
        layout.prefHeightProperty().bind(this.scene.heightProperty());

        layout.widthProperty().addListener((observable, oldValue, newValue) -> {
            layout.setAlignment(Pos.CENTER); // Центрируем элементы при изменении ширины
        });
        layout.heightProperty().addListener((observable, oldValue, newValue) -> {
            layout.setAlignment(Pos.CENTER); // Центрируем элементы при изменении высоты
        });
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

    private void adjustLayout() {
        // Центрируем содержимое при изменении размеров окна
        System.out.println("a[a[a[a[a[a[");
        layout.setAlignment(Pos.CENTER);
    }
}
