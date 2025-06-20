package ru.nsu.t4werok.towerdefence.view.game.entities.tower;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerConfig;
import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechTree;
import ru.nsu.t4werok.towerdefence.view.game.playerState.tech.TechTreeView;

import java.util.List;

public class TowerView {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final GameController gameController;
    private final TechTreeView techTreeView;
    private final GameMap gameMap;
    public TowerView(GameController gameController, GraphicsContext gc, Canvas canvas, GameMap gameMap) {
        this.gc = gc;
        this.canvas = canvas;
        this.gameController = gameController;
        this.gameMap = gameMap;
        this.techTreeView = new TechTreeView(this.gameController);
    }

    public void viewTowersForSelect(VBox towerListPanel) {
        Accordion accordion = new Accordion();

        for (TowerConfig towerConfig : gameController.getTowersForSelect()) {
            VBox content = new VBox(5);

            Button selectButton = new Button("Select");
            selectButton.setOnAction(e -> gameController.selectTower(towerConfig));

            Button upgradesButton = new Button("Upgrades");
            upgradesButton.setOnAction(e -> techTreeView.showUpgradesWindow(towerConfig));

            content.getChildren().addAll(selectButton, upgradesButton);

            TitledPane pane = new TitledPane(towerConfig.getName(), content);
            accordion.getPanes().add(pane);
        }

        towerListPanel.getChildren().add(accordion);
    }

    public void renderTower(Tower tower) {
        // Получаем изображение для башни
        Image towerImage = tower.getImageTower();

        // Проверяем, если изображение отсутствует, не рисуем ничего
        if (towerImage == null) {
            System.out.println("Tower image is missing for tower: " + tower.getName());
            return;
        }

        int cellWidth = (int) (canvas.getWidth() / gameMap.getWidth());
        int cellHeight = (int) (canvas.getHeight() / gameMap.getHeight());

        // Получаем координаты для отрисовки башни
        int towerX = tower.getX();  // X-координата расположения башни (в клетках)
        int towerY = tower.getY();  // Y-координата расположения башни (в клетках)


        // Сжимаем изображение до размера клетки
        double towerWidth = Math.min(towerImage.getWidth(), cellWidth);
        double towerHeight = Math.min(towerImage.getHeight(), cellHeight);

        // Преобразуем координаты в пиксели, умножив на размер клетки
        double pixelX = towerX * cellWidth;
        double pixelY = towerY * cellHeight;

        // Отображаем изображение башни на канвасе с заданными координатами и размерами
        gc.drawImage(towerImage, pixelX, pixelY, towerWidth, towerHeight);
    }

    public void renderBullets(Tower tower, int numBullets) {
        // Координаты башни
        double startX = (tower.getX() + 0.5) * (canvas.getWidth() / gameMap.getWidth());
        double startY = (tower.getY() + 0.5) * (canvas.getHeight() / gameMap.getHeight());

        // Координаты врага
        double endX = tower.getAttackX();
        double endY = tower.getAttackY();

        // Вычисляем шаг между шариками
        double deltaX = (endX - startX) / (numBullets - 1);
        double deltaY = (endY - startY) / (numBullets - 1);

        // Рисуем шарики
        gc.setFill(Color.BLACK);
        for (int i = 0; i < numBullets; i++) {
            double bulletX = startX + i * deltaX;
            double bulletY = startY + i * deltaY;

            gc.fillOval(bulletX - 5, bulletY - 5, 10, 10); // Рисуем круг диаметром 10
        }
    }

    public void showTowerUpgradeMenu(Tower tower, List<TechTree> techTrees) {
        techTreeView.showTowerUpgradeMenu(tower, techTrees);
    }
}
