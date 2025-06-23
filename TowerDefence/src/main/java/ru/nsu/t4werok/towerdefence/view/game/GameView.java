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
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.view.game.entities.map.MapView;
import ru.nsu.t4werok.towerdefence.view.game.entities.tower.TowerView;

public class GameView {
    private final Scene scene;
    private final Canvas canvas; // Полотно для рисования
    private final VBox towerListPanel; // Панель для отображения доступных башен
    private final GameMap gameMap; // Ссылка на карту для отображения
    private final MapView mapView;
    private final TowerView towerView;
    private Stage menuStage;
    private final Button startButton;


    private final GraphicsContext gc;


    public GameView(GameController gameController, GameEngine engine) {
        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();

        gameMap = gameController.getGameMap();
        mapView = new MapView(gc, canvas, gameMap);

        towerView = new TowerView(gameController, gc, canvas, gameMap);

        // Панель для башен справа
        towerListPanel = new VBox(10);
        towerListPanel.setStyle("-fx-background-color: #D3D3D3; -fx-padding: 10;");
        towerListPanel.setPrefWidth(300);
        towerListPanel.setPrefHeight(600);

        // Башни на выбор и прокачки
        gameController.loadTowersForSelect();
        towerView.viewTowersForSelect(towerListPanel);


        startButton = new Button("Start");
        startButton.setOnAction(e -> {
            // Логика первого нажатия
            engine.start(); // Запуск игры
            startButton.setText("NextWave"); // Изменение текста кнопки

            // Изменение действия кнопки на "NextWave"
            startButton.setOnAction(event -> {
                if(engine.nextWave()){
                    deleteButtonNextWave();// Вызов следующей волны
                }
                System.out.println("Next wave started!");
            });
        });

        // Добавляем кнопку в панель башен
        towerListPanel.getChildren().add(startButton);

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
        mapView.renderMap();
        mapView.renderHUD(gameController.coinsNow(), gameController.getGameMap().getBase().getHealth());



        // Обработчик клика по канвасу (для добавления башни)
        canvas.setOnMouseClicked(e -> {
            // Получаем координаты клика
            int x = (int) e.getX();
            int y = (int) e.getY();

            int cellWidth = (int) (canvas.getWidth() / gameMap.getWidth());
            int cellHeight = (int) (canvas.getHeight() / gameMap.getHeight());

            int towerXCell = x / cellWidth;  // Координата клетки по оси X
            int towerYCell = y / cellHeight; // Координата клетки по оси Y

            if (gameController.getSelectedTower() != null) {
                Tower tower = gameController.placeTower(towerXCell, towerYCell);
                if (tower != null) {
                    towerView.renderTower(tower);
                } else {
                    gameController.setSelectedTower(null);
                }
            }

            if (gameController.getSelectedTower() == null && gameController.checkTowerInCell(towerXCell, towerYCell)) {
                Tower tower = gameController.getTowerAtCell(towerXCell, towerYCell);
                if (tower != null) {
                    towerView.showTowerUpgradeMenu(tower, gameController.getTechTrees());
                }
            }
//            else {
//                gameController.setSelectedTower(null);
//            }
//            gameController.setSelectedTower(null);


        });

        scene.setOnMouseClicked(e -> {
            mapView.renderHUD(gameController.coinsNow(), gameController.getGameMap().getBase().getHealth());
        });
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

    public void deleteButtonNextWave(){
        towerListPanel.getChildren().remove(startButton);
    }

    public Scene getScene() {
        return scene;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public GraphicsContext getGc() {
        return gc;
    }

    public MapView getMapView() {
        return mapView;
    }
}
