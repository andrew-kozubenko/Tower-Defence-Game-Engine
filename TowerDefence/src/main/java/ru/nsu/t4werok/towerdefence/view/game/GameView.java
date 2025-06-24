package ru.nsu.t4werok.towerdefence.view.game;

import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.app.GameEngine;
import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.view.game.entities.map.MapView;
import ru.nsu.t4werok.towerdefence.view.game.entities.tower.TowerView;

public class GameView {
    private final Scene scene;
    private final Canvas canvas;
    private final VBox towerListPanel;
    private final GameMap gameMap;
    private final MapView mapView;
    private final TowerView towerView;
    private Stage menuStage;
    private final Button nextWaveBtn;
    private final GraphicsContext gc;

    private final StackPane rootStack;
    private final Pane gameOverOverlay;

    private boolean gameOverShown = false;

    public GameView(GameController gameController, GameEngine engine) {
        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();

        gameMap = gameController.getGameMap();
        mapView = new MapView(gc, canvas, gameMap);
        towerView = new TowerView(gameController, gc, canvas, gameMap);

        towerListPanel = new VBox(10);
        towerListPanel.setStyle("-fx-background-color: #D3D3D3; -fx-padding: 10;");
        towerListPanel.setPrefWidth(300);
        towerListPanel.setPrefHeight(600);

        gameController.loadTowersForSelect();
        towerView.viewTowersForSelect(towerListPanel);



        nextWaveBtn = new Button("Next wave");

        nextWaveBtn.setOnAction(e -> {
            if (engine.isWaveInProgress() ) return; // волна идёт — ничего не делаем

            if (!engine.nextWave()) {
                towerListPanel.getChildren().remove(nextWaveBtn);
            } else {
                nextWaveBtn.setDisable(true); // блокируем до завершения
            }
        });
        if (engine.isiAmHost() || engine.getSession() == null ){
            towerListPanel.getChildren().add(nextWaveBtn);
        }


        Button menuButton = new Button("Menu");
        menuButton.setOnAction(e -> showMenu(gameController));
        HBox topBar = new HBox(10, menuButton);

        StackPane canvasContainer = new StackPane(canvas);
        HBox rootLayout = new HBox(10, topBar, canvasContainer, towerListPanel);
        rootLayout.setStyle("-fx-padding: 10;");

        // Game Over overlay (невидим по умолчанию)
        gameOverOverlay = new Pane();
        gameOverOverlay.setMouseTransparent(true);
        gameOverOverlay.setPickOnBounds(true);
        gameOverOverlay.setVisible(false);
        gameOverOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
        gameOverOverlay.setPrefSize(1024, 768);

        VBox overlayContent = new VBox(20);
        overlayContent.setAlignment(Pos.CENTER);
        overlayContent.setPrefSize(1024, 768);

        Label gameOverLabel = new Label("Game Over");
        gameOverLabel.setTextFill(Color.WHITE);
        gameOverLabel.setFont(new Font(48));

        Button backToMenuBtn = new Button("Main Menu");
        backToMenuBtn.setOnAction(e -> {
            gameController.backToMenu(menuStage);
        });

        overlayContent.getChildren().addAll(gameOverLabel, backToMenuBtn);
        gameOverOverlay.getChildren().add(overlayContent);

        // Слой над всей игрой
        rootStack = new StackPane();
        rootStack.getChildren().addAll(rootLayout, gameOverOverlay);
        scene = new Scene(rootStack, 1024, 768);

        scene.setOnKeyPressed(e -> {
            if (gameOverShown) return;
            switch (e.getCode()) {
                case P -> gameController.stop();
                case Q -> System.exit(0);
            }
        });

        canvas.setOnMouseClicked(e -> {
            if (gameOverShown) return;

            int x = (int) e.getX();
            int y = (int) e.getY();
            int cellWidth = (int) (canvas.getWidth() / gameMap.getWidth());
            int cellHeight = (int) (canvas.getHeight() / gameMap.getHeight());

            int towerXCell = x / cellWidth;
            int towerYCell = y / cellHeight;

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
        });

        scene.setOnMouseClicked(e -> {
            if (!gameOverShown) {
                mapView.renderHUD(gameController.coinsNow(), gameController.getGameMap().getBase().getHealth());
            }
        });

        mapView.renderMap();
        mapView.renderHUD(gameController.coinsNow(), gameController.getGameMap().getBase().getHealth());
    }

    private void showMenu(GameController gameController) {
        this.menuStage = new Stage();
        VBox menuLayout = new VBox(10);
        menuLayout.setStyle("-fx-padding: 20;");

        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(e -> gameController.openSettings(menuStage));
        Button backGameButton = new Button("Back to game");
        backGameButton.setOnAction(e -> gameController.backToGame(menuStage));
        Button backMenuButton = new Button("Back to menu");
        backMenuButton.setOnAction(e -> gameController.backToMenu(menuStage));
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> System.exit(0));

        menuLayout.getChildren().addAll(settingsButton, backGameButton, backMenuButton, exitButton);
        Scene menuScene = new Scene(menuLayout, 200, 150);
        menuStage.setScene(menuScene);
        menuStage.setTitle("Game Menu");
        menuStage.show();
    }

    public void deleteButtonNextWave() {
        towerListPanel.getChildren().remove(nextWaveBtn);
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

    public void showGameOverOverlay() {
        gameOverOverlay.setVisible(true);
        gameOverOverlay.setMouseTransparent(false);
        gameOverShown = true;
    }

    public void update(GameEngine engine) {
        // если волна завершилась — разблокируем кнопку
        if (!engine.isWaveInProgress()) {
            nextWaveBtn.setDisable(false);
        }
    }

}
