package ru.nsu.t4werok.towerdefence.view.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenuView {

    private final Stage primaryStage;

    public MainMenuView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        // Создаем корневой контейнер
        VBox root = new VBox(15); // VBox с отступом 15
        root.setAlignment(Pos.CENTER_LEFT); // Центровка элементов

        // Создаем кнопки для главного меню
        Button startButton = new Button("Start Game");
        Button replayButton = new Button("Replay");
        Button settingsButton = new Button("Settings");
        Button exitButton = new Button("Exit");

        // Назначаем обработчики событий
        startButton.setOnAction(e -> new MapSelectionView(primaryStage).show());
        replayButton.setOnAction(e -> new ReplaySelectionView(primaryStage).show());
        settingsButton.setOnAction(e -> new SettingsView(primaryStage).show());
        exitButton.setOnAction(e -> primaryStage.close());

        // Добавляем кнопки в VBox
        root.getChildren().addAll(startButton, replayButton, settingsButton, exitButton);

        // Создаем сцену и задаем ее для stage
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Tower Defence - Main Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
