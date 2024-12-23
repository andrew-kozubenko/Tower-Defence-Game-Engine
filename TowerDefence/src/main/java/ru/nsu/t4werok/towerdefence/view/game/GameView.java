package ru.nsu.t4werok.towerdefence.view.game;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerSelectionConfig;
import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.view.game.entities.map.MapView;

import java.util.List;

public class GameView {
    private final Scene scene;
    private final Canvas canvas; // Полотно для рисования
    private final VBox towerListPanel; // Панель для отображения доступных башен
    private final GameMap gameMap; // Ссылка на карту для отображения
    private final MapView mapView;
    private Stage menuStage;

    public GameView(GameController gameController) {
        canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gameMap = gameController.getGameMap();

        mapView = new MapView(canvas, gameMap);

        // Панель для башен справа
        towerListPanel = new VBox(10);
        towerListPanel.setStyle("-fx-background-color: #D3D3D3; -fx-padding: 10;");
        towerListPanel.setPrefWidth(150);
        towerListPanel.setPrefHeight(600);

        TowerSelectionConfig towerConfigLoader = new TowerSelectionConfig();
        List<TowerConfig> towerConfigs = towerConfigLoader.loadTowers();

        // Для каждой башни создаем кнопку
        for (TowerConfig towerConfig : towerConfigs) {
            Button towerButton = new Button(towerConfig.getName());  // Используем имя из конфигурации
            towerButton.setOnAction(e -> gameController.selectTower(towerConfig));
            towerListPanel.getChildren().add(towerButton);
        }

        // Создание кнопки для меню
        Button menuButton = new Button("Menu");
        menuButton.setOnAction(e -> showMenu(gameController));

        // Добавляем кнопки в панель управления
        HBox topBar = new HBox(10);
        topBar.getChildren().add(menuButton);

        // Добавляем в root контейнер
        StackPane layout = new StackPane(canvas);
        HBox rootLayout = new HBox(10, topBar, layout, towerListPanel);
        rootLayout.setStyle("-fx-padding: 10;");
        this.scene = new Scene(rootLayout, 1024, 768);

        // Добавляем обработчики событий
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case P -> gameController.stop(); // Пауза игры
                case Q -> System.exit(0);        // Выход из игры
            }
        });

        // Рисуем карту
        mapView.renderMap(gc);
    }

    private void showMenu(GameController gameController) {
        this.menuStage = new Stage();
        VBox menuLayout = new VBox(10);
        menuLayout.setStyle("-fx-padding: 20;");

        // Кнопки в меню
        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(e -> gameController.openSettings(menuStage));
        Button backGameButton = new Button("Back to game");
        backGameButton.setOnAction(e -> gameController.backToGame(menuStage));
        Button backMenuButton = new Button("Back to menu");
        backMenuButton.setOnAction(e -> gameController.backToMenu(menuStage));
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> System.exit(0));

        // Добавляем кнопки в меню
        menuLayout.getChildren().addAll(settingsButton, backGameButton, backMenuButton, exitButton);

        // Создаём сцену для меню
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
