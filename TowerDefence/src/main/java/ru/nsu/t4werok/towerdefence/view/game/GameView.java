package ru.nsu.t4werok.towerdefence.view.game;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.app.GameEngine;
import ru.nsu.t4werok.towerdefence.controller.game.GameController;

public class GameView {
    private final Scene scene;
    private final Canvas canvas; // Полотно для рисования
    private final VBox towerListPanel; // Панель для отображения доступных башен

    public GameView(GameController gameController) {
        // Инициализация игрового полотна
        canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Панель для башен справа
        towerListPanel = new VBox(10); // Интервал между кнопками
        towerListPanel.setStyle("-fx-background-color: #D3D3D3; -fx-padding: 10;");
        towerListPanel.setPrefWidth(150); // Задаем ширину панели
        towerListPanel.setPrefHeight(600); // Высота должна совпадать с игровым полем

        // Пример доступных башен
        Button arrowTowerButton = new Button("Arrow Tower");
        arrowTowerButton.setOnAction(e -> gameController.selectTower("Arrow Tower"));
        Button cannonTowerButton = new Button("Cannon Tower");
        cannonTowerButton.setOnAction(e -> gameController.selectTower("Cannon Tower"));

        // Добавление кнопок для башен в панель
        towerListPanel.getChildren().addAll(arrowTowerButton, cannonTowerButton);

        // Создание кнопки для меню
        Button menuButton = new Button("Menu");
        menuButton.setOnAction(e -> showMenu(gameController));

        // Добавляем кнопки в панель управления
        HBox topBar = new HBox(10);
        topBar.getChildren().add(menuButton);

        // Создание основного контейнера
        StackPane layout = new StackPane(canvas);
        HBox rootLayout = new HBox(10, layout, towerListPanel); // Добавление панели с башнями справа
        rootLayout.setStyle("-fx-padding: 10;");
        this.scene = new Scene(rootLayout, 1024, 768); // Устанавливаем размер окна игры

        // Добавляем обработчики событий (например, для управления)
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case P -> gameController.stop(); // Пример: пауза игры
                case Q -> System.exit(0);   // Выход из игры
            }
        });
    }

    // Метод для отображения меню
    private void showMenu(GameController gameController) {
        Stage menuStage = new Stage();
        VBox menuLayout = new VBox(10);
        menuLayout.setStyle("-fx-padding: 20;");

        // Кнопки в меню
        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(e -> gameController.openSettings());
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> gameController.goBack());
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> System.exit(0));

        // Добавление кнопок в меню
        menuLayout.getChildren().addAll(settingsButton, backButton, exitButton);

        // Создаем сцену для меню
        Scene menuScene = new Scene(menuLayout, 200, 150);
        menuStage.setScene(menuScene);
        menuStage.setTitle("Game Menu");
        menuStage.show();
    }

    public Scene getScene() {
        return scene;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}


