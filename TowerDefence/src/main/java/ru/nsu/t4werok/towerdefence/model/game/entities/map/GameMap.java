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
    private Image backgroundImage = null;                    // Фон карты
    private Image baseImage = null;
    private Image towerImage = null;
    private Image spawnPointImage = null;

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
                   String backgroundImagePath,
                   String baseImagePath,
                   String towerImagePath,
                   String spawnPointImagePath) {
        this.width = width;
        this.height = height;
        this.enemyPaths = enemyPaths;
        this.towerPositions = towerPositions;
        this.spawnPoint = spawnPoint;
        this.base = base;
        this.backgroundImage = loadImage(backgroundImagePath);
        this.baseImage = loadImage(baseImagePath);
        this.towerImage = loadImage(towerImagePath);
        this.spawnPointImage = loadImage(spawnPointImagePath);
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

    public Image getBaseImage() {
        return baseImage;
    }

    public void setBaseImage(Image baseImage) {
        this.baseImage = baseImage;
    }

    public Image getTowerImage() {
        return towerImage;
    }

    public void setTowerImage(Image towerImage) {
        this.towerImage = towerImage;
    }

    public Image getSpawnPointImage() {
        return spawnPointImage;
    }

    public void setSpawnPointImage(Image spawnPointImage) {
        this.spawnPointImage = spawnPointImage;
    }

    private Image loadImage(String path) {
        // Формируем путь в Documents/Games/TowerDefenceSD
        Path docBasePath = Paths.get(
                System.getProperty("user.home"),
                "Documents",
                "Games",
                "TowerDefenceSD"
        );
        // Дополняем его относительным путем ImagePath
        Path fullImagePath = docBasePath.resolve(path);

        Image image = null;
        // Пытаемся загрузить файл
        try {
            File file = fullImagePath.toFile();
            if (file.exists()) {
                image = new Image(file.toURI().toString());
            } else {
                System.err.println("Background image file not found: " + fullImagePath);
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
        }

        return image;
    }

    /**
     * Проверка, находится ли точка (x, y) в пределах карты
     */
    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
