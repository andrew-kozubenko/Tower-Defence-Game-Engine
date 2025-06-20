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

        int cellWidth = (int) (canvas.getWidth() / gameMap.getWidth());
        int cellHeight = (int) (canvas.getHeight() / gameMap.getHeight());

        // Отрисовка сетки
        renderGrid(gameMap, cellWidth, cellHeight);

        // Отрисовка доступных позиций для башен
        renderTowerPositions(gameMap, cellWidth, cellHeight);

        // Отрисовка пути врагов
        renderEnemyPaths(gameMap, cellWidth, cellHeight);

        // Отрисовка базы
        renderBase(gameMap, cellWidth, cellHeight);

        // Отрисовка точки спавна
        renderSpawnPoint(gameMap, cellWidth, cellHeight);
    }

    private void renderGrid(GameMap gameMap, int cellWidth, int cellHeight) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.5); // Тонкие линии для сетки
        for (int x = 0; x <= gameMap.getWidth(); x++) {
            gc.strokeLine(x * cellWidth, 0, x * cellWidth, canvas.getHeight());
        }
        for (int y = 0; y <= gameMap.getHeight(); y++) {
            gc.strokeLine(0, y * cellHeight, canvas.getWidth(), y * cellHeight);
        }
    }

    private void renderTowerPositions(GameMap gameMap, int cellWidth, int cellHeight) {
        if (gameMap.getTowerPositions() != null) {
            Image towerImage = gameMap.getTowerImage();
            for (Integer[] position : gameMap.getTowerPositions()) {
                gc.drawImage(towerImage,
                        position[0] * cellWidth, position[1] * cellHeight, cellWidth, cellHeight);

            }
        }
    }

    private void renderEnemyPaths(GameMap gameMap, int cellWidth, int cellHeight) {
        if (gameMap.getEnemyPaths() != null) {
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
        }
    }

    private void renderBase(GameMap gameMap, int cellWidth, int cellHeight) {
        if (gameMap.getBase() != null) {
            int baseX = gameMap.getBase().getX();
            int baseY = gameMap.getBase().getY();
            Image baseImage = gameMap.getBaseImage();
            gc.drawImage(baseImage,
                    baseX * cellWidth, baseY * cellHeight, cellWidth, cellHeight); // Рисуем базу
        }
    }

    private void renderSpawnPoint(GameMap gameMap, int cellWidth, int cellHeight) {
        if (gameMap.getSpawnPoint() != null) {
            Integer[] spawn = gameMap.getSpawnPoint();
            Image spawnPoinImage = gameMap.getSpawnPointImage();
            gc.drawImage(spawnPoinImage,
                    spawn[0] * cellWidth, spawn[1] * cellHeight, cellWidth, cellHeight);// Рисуем точку спавна
        }
    }

    public void renderHUD(int coins, int health) {
        // Отрисовка прямоугольника с информацией (деньги и здоровье)
        double rectX = canvas.getWidth() - 150; // Позиция прямоугольника
        double rectY = 0;
        double rectWidth = 130;
        double rectHeight = 40;

        gc.setFill(Color.WHITE);
        gc.fillRect(rectX, rectY, rectWidth, rectHeight);

        gc.setStroke(Color.BLACK);
        gc.strokeRect(rectX, rectY, rectWidth, rectHeight);

        String coinsText = coins + "$ " + health + "HP";
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(16));
        gc.fillText(coinsText, rectX + 10, rectY + 25);
    }

}
