package ru.nsu.t4werok.towerdefence.view.game.entities.map;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;

import java.util.List;

public class MapView {
    private final GameMap gameMap;
    private final Canvas canvas;
    private final GraphicsContext gc;

    public MapView(GraphicsContext gc, Canvas canvas, GameMap gameMap) {
        this.gc = gc;
        this.canvas = canvas;
        this.gameMap = gameMap;
    }

    public void renderMap() {
        // Отображение фона карты
        Image backgroundImage = gameMap.getBackgroundImage();
        if (backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, 0, canvas.getWidth(), canvas.getHeight());
        } else {
            System.out.println("No background image found, using default.");
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }

        // Отрисовка сетки
        int cellWidth = (int) (canvas.getWidth() / gameMap.getWidth());
        int cellHeight = (int) (canvas.getHeight() / gameMap.getHeight());
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.5); // Тонкие линии для сетки
        for (int x = 0; x <= gameMap.getWidth(); x++) {
            gc.strokeLine(x * cellWidth, 0, x * cellWidth, canvas.getHeight());
        }
        for (int y = 0; y <= gameMap.getHeight(); y++) {
            gc.strokeLine(0, y * cellHeight, canvas.getWidth(), y * cellHeight);
        }

        // Отрисовка доступных позиций для башен
        gc.setFill(Color.GREEN);
        for (Integer[] position : gameMap.getTowerPositions()) {
            gc.fillRect(position[0] * cellWidth, position[1] * cellHeight, cellWidth, cellHeight);
        }

        // Отрисовка пути врагов
        gc.setStroke(Color.RED);
        gc.setLineWidth(3);
        for (List<Integer[]> path : gameMap.getEnemyPaths()) {
            for (int i = 0; i < path.size() - 1; i++) {
                Integer[] start = path.get(i);
                Integer[] end = path.get(i + 1);
                gc.strokeLine(
                        start[0] * cellWidth + cellWidth / 2, start[1] * cellHeight + cellHeight / 2,
                        end[0] * cellWidth + cellWidth / 2, end[1] * cellHeight + cellHeight / 2
                );
            }
        }

        // Отрисовка базы
        gc.setFill(Color.BLUE);
        int baseX = gameMap.getBase().getX();
        int baseY = gameMap.getBase().getY();
        gc.fillRect(baseX * cellWidth, baseY * cellHeight, cellWidth, cellHeight); // Рисуем базу
    }
}
