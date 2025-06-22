package ru.nsu.t4werok.towerdefence.app;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.nsu.t4werok.towerdefence.config.game.entities.enemy.EnemiesConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.enemy.WavesConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerConfig;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.controller.game.entities.enemy.WaveController;
import ru.nsu.t4werok.towerdefence.controller.game.entities.tower.TowerController;
import ru.nsu.t4werok.towerdefence.controller.menu.SettingsController;
import ru.nsu.t4werok.towerdefence.managers.menu.SettingsManager;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Wave;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.Base;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.network.client.GameClient;
import ru.nsu.t4werok.towerdefence.network.server.GameServer;
import ru.nsu.t4werok.towerdefence.utils.ResourceManager;
import ru.nsu.t4werok.towerdefence.view.game.GameView;
import ru.nsu.t4werok.towerdefence.view.game.entities.enemy.AllEnemiesView;
import ru.nsu.t4werok.towerdefence.view.game.entities.tower.TowerView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static ru.nsu.t4werok.towerdefence.utils.EnemiesLoader.loadEnemiesConfig;
import static ru.nsu.t4werok.towerdefence.utils.WavesLoader.loadWavesConfig;

public class GameEngine {
    private final GameMap gameMap; // Карта, на которой происходит игра
    private boolean running = false; // Флаг состояния игры
    private AnimationTimer gameLoop; // Игровой цикл
    private final List<Enemy> enemies = new ArrayList<>(); // Список врагов
    private final List<Tower> towers = new ArrayList<>(); // Список башен на карте
    private Base base; // База

    private int waveNumber = 0; // Номер текущей волны

    private static final double TARGET_FPS = 30.0; // Целевая частота кадров
    private static final double TARGET_TIME_PER_FRAME = 1_000_000_000.0 / TARGET_FPS; // Время на один кадр (в наносекундах)

    private long lastUpdateTime = 0; // Время последнего обновления

    private final GameController gameController;
    private final GameView gameView;
    private final AllEnemiesView allEnemiesView;
    private final SceneController sceneController;
    private final TowerView towerView;
    private final SettingsManager settingsManager = SettingsManager.getInstance();

    private final EnemiesConfig enemiesConfig;
    private final WavesConfig wavesConfig;
    private final WaveController waveController;

    private final boolean isMultiplayer;
    private final GameClient client;
    private final GameServer server;

    public GameEngine(GameMap gameMap, SceneController sceneController, Base base) {
        this(gameMap, sceneController, base, false, null, null);
    }

    public GameEngine(GameMap gameMap, SceneController sceneController, Base base, GameServer server) {
        this(gameMap, sceneController, base, true, null, server);
    }

    public GameEngine(GameMap gameMap, SceneController sceneController, Base base, GameClient client) {
        this(gameMap, sceneController, base, true, client, null);
    }

    public GameEngine(GameMap gameMap, SceneController sceneController, Base base,
                      boolean isMultiplayer, GameClient client, GameServer server) {
        this.gameMap = gameMap;
        this.sceneController = sceneController;
        this.base = base; // Передаем базу
        this.isMultiplayer = isMultiplayer;
        this.client = client;
        this.server = server;

        // Подготавливаем контроллеры
        this.gameController = new GameController(this, sceneController, gameMap, towers);
        this.gameView = new GameView(gameController, this);
        sceneController.addScene("Game", gameView.getScene());
        sceneController.switchTo("Game");
        allEnemiesView = new AllEnemiesView();
        towerView = new TowerView(gameController, gameView.getGc(), gameView.getCanvas(), gameMap);

        // Формируем путь к стандартной папке: Documents/Games/TowerDefenceSD
        Path baseConfigPath = Paths.get(System.getProperty("user.home"), "Documents", "Games", "TowerDefenceSD");

        // Убедимся, что папка существует (если нужно также создавать при отсутствии)
        try {
            if (!Files.exists(baseConfigPath)) {
                Files.createDirectories(baseConfigPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директории для конфигураций: " + e.getMessage());
        }

        // Путь к файлам с конфигурациями
        Path enemiesPath = ResourceManager.getEnemiesConfigFile();
        Path wavesPath   = ResourceManager.getWavesConfigFile();

        try {
            this.enemiesConfig = loadEnemiesConfig(enemiesPath.toString());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки enemies.json: " + e.getMessage(), e);
        }

        try {
            this.wavesConfig = loadWavesConfig(wavesPath.toString());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки waves.json: " + e.getMessage(), e);
        }

        waveController = new WaveController(wavesConfig, enemiesConfig, gameMap.getEnemyPaths().size());
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void start() {
        running = true;
        settingsManager.setRunningGame(true);
        System.out.println("Start");

        // Игровой цикл
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!running) {
                    settingsManager.setRunningGame(false);
                    stop();
                    return;
                }

                // Ограничение частоты обновлений
                long deltaTime = now - lastUpdateTime;
                if (deltaTime >= TARGET_TIME_PER_FRAME) {
                    lastUpdateTime = now; // Обновляем время последнего обновления
                    update(); // Обновление состояния игры
                    render(); // Отрисовка объектов
                }
            }
        };
        gameLoop.start();
    }

    public void stop() {
        running = false;
        gameView.deleteButtonNextWave();
    }

    public void update() {
        if (!running) return;

        // Обновляем текущую волну. Если волны кончились и врагов нет - останавливаем игру.
        if (!waveController.updateWave(enemies, gameMap.getSpawnPoint(),
                800 / gameMap.getWidth(), 600 / gameMap.getHeight()) && enemies.isEmpty()) {
            stop();
            return;
        }

        // Обновление врагов
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            enemy.move(gameMap); // Двигаем врагов по пути
            if (enemy.isDead()) {
                if (base != null) {
                    base.takeDamage(enemy.getDamageToBase()); // Наносим урон базе, если враг достиг её
                    enemies.remove(enemy);
                }
            }
        }

        // Обновление башен
        gameController.updateTower(enemies);

        // Проверка поражения
        if (base.getHealth() <= 0) {
            stop();
            System.out.println("Game Over!");
        }
    }

    private void render() {
        GraphicsContext gc = gameView.getGc();
        Canvas canvas = gameView.getCanvas();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gameView.getMapView().renderMap();

        for (Tower tower : towers) {
            towerView.renderTower(tower);
            if (tower.getAttackX() != -1) {
                towerView.renderBullets(tower, 5);
                tower.setAttackX(-1);
                tower.setAttackY(-1);
            }
        }

        // Отрисовываем врагов
        allEnemiesView.renderEnemies(gc, enemies);

        // Отрисовка прямоугольника с информацией (деньги и здоровье)
        double rectX = canvas.getWidth() - 150; // Позиция прямоугольника
        double rectY = 0;
        double rectWidth = 130;
        double rectHeight = 40;

        gc.setFill(Color.WHITE);
        gc.fillRect(rectX, rectY, rectWidth, rectHeight);

        gc.setStroke(Color.BLACK);
        gc.strokeRect(rectX, rectY, rectWidth, rectHeight);

        String coinsText = gameController.coinsNow() + "$ " + base.getHealth() + "HP";
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(16));
        gc.fillText(coinsText, rectX + 10, rectY + 25);
    }

    public boolean nextWave() {
        return waveController.nextWave();
    }
}
