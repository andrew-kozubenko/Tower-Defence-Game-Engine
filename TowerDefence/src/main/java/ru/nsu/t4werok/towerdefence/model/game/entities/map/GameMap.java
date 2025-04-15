package ru.nsu.t4werok.towerdefence.model.game.entities.map;

import javafx.scene.image.Image;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.Base;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GameMap {
    private final Integer width;     // Ширина карты
    private final Integer height;    // Высота карты
    private final List<List<Integer[]>> enemyPaths;   // Пути врагов
    private final List<Integer[]> towerPositions;     // Доступные позиции для башен
    private final Integer[] spawnPoint;               // Точка появления врагов
    private Base base;
    private Image backgroundImage;                    // Фон карты

    /**
     * @param backgroundImagePath относительный путь к файлу (например, "maps/background.jpg"),
     *                            который будет взят из "<user_home>/Documents/Games/TowerDefenceSD/...".
     */
    public GameMap(Integer width,
                   Integer height,
                   List<List<Integer[]>> enemyPaths,
                   List<Integer[]> towerPositions,
                   Integer[] spawnPoint,
                   Base base,
                   String backgroundImagePath) {
        this.width = width;
        this.height = height;
        this.enemyPaths = enemyPaths;
        this.towerPositions = towerPositions;
        this.spawnPoint = spawnPoint;
        this.base = base;

        // Формируем путь в Documents/Games/TowerDefenceSD
        Path docBasePath = Paths.get(
                System.getProperty("user.home"),
                "Documents",
                "Games",
                "TowerDefenceSD"
        );
        // Дополняем его относительным путём backgroundImagePath
        Path fullImagePath = docBasePath.resolve(backgroundImagePath);

        // Пытаемся загрузить файл
        try {
            File file = fullImagePath.toFile();
            if (file.exists()) {
                this.backgroundImage = new Image(file.toURI().toString());
            } else {
                System.err.println("Background image file not found: " + fullImagePath);
                this.backgroundImage = null;
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
            this.backgroundImage = null;
        }
    }

    // --- Геттеры и сеттеры ---

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public List<List<Integer[]>> getEnemyPaths() {
        return enemyPaths;
    }

    public List<Integer[]> getTowerPositions() {
        return towerPositions;
    }

    public Integer[] getSpawnPoint() {
        return spawnPoint;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    /**
     * Проверка, находится ли точка (x, y) в пределах карты
     */
    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
