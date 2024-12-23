package ru.nsu.t4werok.towerdefence.view.game.entities.tower;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerConfig;
import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.view.game.playerState.tech.TechTreeView;

public class TowerView {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final GameController gameController;
    private final TechTreeView techTreeView = new TechTreeView();

    public TowerView(GameController gameController, GraphicsContext gc, Canvas canvas) {
        this.gc = gc;
        this.canvas = canvas;
        this.gameController = gameController;
    }

    public void viewTowersForSelect(VBox towerListPanel) {
        for (TowerConfig towerConfig : gameController.getTowersForSelect()) {
            VBox towerBox = new VBox(5); // Контейнер для кнопок башни и её апгрейдов

            // Кнопка для выбора башни
            Button towerButton = new Button(towerConfig.getName());
            towerButton.setOnAction(e -> gameController.selectTower(towerConfig));

            // Кнопка для открытия окна улучшений
            Button upgradesButton = new Button("Upgrades");
            upgradesButton.setOnAction(e -> techTreeView.showUpgradesWindow(gameController, towerConfig));

            // Добавляем кнопки в контейнер
            towerBox.getChildren().addAll(towerButton, upgradesButton);

            // Добавляем контейнер в панель
            towerListPanel.getChildren().add(towerBox);
        }
    }

    public void renderTower(Tower tower) {
        // Получаем изображение для башни
        Image towerImage = tower.getImageTower();  // Это изображение, которое нужно отрисовать. Возможно, нужно будет создать метод getImage() в классе Tower.

        if (towerImage != null) {
            // Размеры изображения до сжатия
            double originalWidth = towerImage.getWidth();
            double originalHeight = towerImage.getHeight();

            // Сжимаем изображение в 20 раз
            double scaleX = 1.0 / 2;  // Масштаб по оси X (сжимаем в 20 раз)
            double scaleY = 1.0 / 2;  // Масштаб по оси Y (сжимаем в 20 раз)

            // Определяем координаты башни на карте
            int towerX = tower.getX();
            int towerY = tower.getY();

            // Преобразуем координаты башни в пиксели на канвасе
            double pixelX = towerX * 2;  // Переводим в пиксели, используя размер клетки
            double pixelY = towerY * 2;

            // Рисуем изображение башни, сжимаем его по осям X и Y
            gc.drawImage(towerImage, pixelX, pixelY, originalWidth * scaleX, originalHeight * scaleY);
        }
    }
}
