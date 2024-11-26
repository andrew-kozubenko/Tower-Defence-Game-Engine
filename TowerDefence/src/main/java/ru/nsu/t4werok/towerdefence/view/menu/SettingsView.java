package ru.nsu.t4werok.towerdefence.view.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsView {
    private final Stage stage;

    public SettingsView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Создаем контейнер VBox
        VBox vbox = new VBox(15); // Расстояние между элементами
        vbox.setAlignment(Pos.CENTER); // Центрируем элементы

        // Кнопка "Resolution"
        Button resolutionButton = new Button("Resolution");
        resolutionButton.setOnAction(e -> handleResolutionChange());

        // Кнопка "Volume"
        Button volumeButton = new Button("Volume");
        volumeButton.setOnAction(e -> handleVolumeChange());

        // Кнопка "Back" для возврата в главное меню
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> new MainMenuView(stage).show());

        // Добавляем кнопки в VBox
        vbox.getChildren().addAll(resolutionButton, volumeButton, backButton);

        // Создаем сцену и задаем ее для stage
        Scene scene = new Scene(vbox, 400, 300);
        stage.setTitle("Settings");
        stage.setScene(scene);
        stage.show();
    }

    private void handleResolutionChange() {
        // Заглушка для изменения разрешения
        System.out.println("Resolution settings will be implemented here.");
    }

    private void handleVolumeChange() {
        // Заглушка для изменения громкости
        System.out.println("Volume settings will be implemented here.");
    }
}


