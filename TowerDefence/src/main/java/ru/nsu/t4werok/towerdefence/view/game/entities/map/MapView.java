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

    public MapView(Canvas canvas, GameMap gameMap) {
        this.canvas = canvas;
        this.gameMap = gameMap;
    }

    public void renderMap(GraphicsContext gc) {
        // Отображение фона карты
        Image backgroundImage = gameMap.getBackgroundImage();
        if (backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, 0, canvas.getWidth(), canvas.getHeight());
        } else {
            System.out.println("No background image found, using default.");
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }

        // Отрисовка доступных позиций для башен
        gc.setFill(Color.GREEN);
        for (Integer[] position : gameMap.getTowerPositions()) {
            gc.fillRect(position[0] * 50, position[1] * 50, 50, 50);
        }

        // Отрисовка пути врагов
        gc.setStroke(Color.RED);
        gc.setLineWidth(3);
        for (List<Integer[]> path : gameMap.getEnemyPaths()) {
            for (int i = 0; i < path.size() - 1; i++) {
                Integer[] start = path.get(i);
                Integer[] end = path.get(i + 1);
                gc.strokeLine(
                        start[0] * 50 + 25, start[1] * 50 + 25,
                        end[0] * 50 + 25, end[1] * 50 + 25
                );
            }
        }

        // Отрисовка базы
        gc.setFill(Color.BLUE);
        int baseX = gameMap.getBase().getX();
        int baseY = gameMap.getBase().getY();
        gc.fillRect(baseX * 50, baseY * 50, 50, 50); // Рисуем базу
    }
}
