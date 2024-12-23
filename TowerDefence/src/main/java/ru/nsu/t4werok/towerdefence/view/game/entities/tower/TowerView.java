package ru.nsu.t4werok.towerdefence.view.game.entities.tower;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;

public class TowerView {

    private final Canvas canvas;
    private final GraphicsContext gc;

    public TowerView(GraphicsContext gc, Canvas canvas) {
        this.gc = gc;
        this.canvas = canvas;
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
