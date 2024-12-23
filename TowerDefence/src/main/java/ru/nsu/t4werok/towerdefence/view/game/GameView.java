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
import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechNode;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechTree;
import ru.nsu.t4werok.towerdefence.view.game.entities.map.MapView;

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

        gameController.loadTowersForSelect();

        // Для каждой башни создаем кнопку
        for (TowerConfig towerConfig : gameController.getTowersForSelect()) {
            VBox towerBox = new VBox(5); // Контейнер для кнопок башни и её апгрейдов

            // Кнопка для выбора башни
            Button towerButton = new Button(towerConfig.getName());
            towerButton.setOnAction(e -> gameController.selectTower(towerConfig));

            // Кнопка для открытия окна улучшений
            Button upgradesButton = new Button("Upgrades");
            upgradesButton.setOnAction(e -> showUpgradesWindow(gameController, towerConfig));

            // Добавляем кнопки в контейнер
            towerBox.getChildren().addAll(towerButton, upgradesButton);

            // Добавляем контейнер в панель
            towerListPanel.getChildren().add(towerBox);
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

    private void showUpgradesWindow(GameController gameController, TowerConfig towerConfig) {
        Stage upgradesStage = new Stage();
        upgradesStage.setTitle("Upgrades for " + towerConfig.getName());

        VBox upgradesLayout = new VBox(10);
        upgradesLayout.setStyle("-fx-padding: 10; -fx-background-color: #F5F5F5;");

        // Получаем дерево технологий для башни
        TechTree techTree = towerConfig.getTechTree();
        if (techTree != null) {
            for (TechNode techNode : techTree.getRoots()) {
                addTechNodeToView(techNode, upgradesLayout, gameController);
            }
        } else {
            upgradesLayout.getChildren().add(new Button("No upgrades available"));
        }

        Scene upgradesScene = new Scene(upgradesLayout, 400, 600);
        upgradesStage.setScene(upgradesScene);
        upgradesStage.show();
    }

    // Рекурсивно добавляет узлы дерева технологий в интерфейс
    private void addTechNodeToView(TechNode node, VBox parentLayout, GameController gameController) {
        HBox nodeBox = new HBox(10);
        nodeBox.setStyle("-fx-padding: 5; -fx-background-color: #E8E8E8; -fx-border-color: #CCCCCC;");
        nodeBox.setPrefHeight(40);

        // Текст с описанием узла
        String description = String.format("%s (Cost: %d)", node.getName(), node.getCost());
        Button techButton = new Button(description);

        // Кнопка для покупки улучшения
        Button buyButton = new Button("Buy");
        buyButton.setOnAction(e -> gameController.buyUpgrade(node));

        // Проверяем доступность узла
        buyButton.setDisable(!gameController.isUpgradeAvailable(node));

        nodeBox.getChildren().addAll(techButton, buyButton);
        parentLayout.getChildren().add(nodeBox);

        // Рекурсивно добавляем дочерние узлы
        for (TechNode child : node.getChildren()) {
            addTechNodeToView(child, parentLayout, gameController);
        }
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
