package ru.nsu.t4werok.towerdefence.app;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
import ru.nsu.t4werok.towerdefence.view.game.GameView;
import ru.nsu.t4werok.towerdefence.view.game.entities.enemy.AllEnemiesView;
import ru.nsu.t4werok.towerdefence.view.game.entities.tower.TowerView;

import java.io.IOException;
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

    public void setRunning(boolean running) {
        this.running = running;
    }

    private Base base; // База

    public GameEngine(GameMap gameMap, SceneController sceneController, Base base) {
        this.gameMap = gameMap;
        this.sceneController = sceneController;
        this.base = base; // Передаем базу
        this.gameController = new GameController(this, sceneController, gameMap, towers);
        this.gameView = new GameView(gameController, this);
        // Добавляем игровую сцену в SceneController
        sceneController.addScene("Game", gameView.getScene());
        // Переключаемся на игровую сцену
        sceneController.switchTo("Game");
        allEnemiesView = new AllEnemiesView();
        towerView = new TowerView(gameController, gameView.getGc(), gameView.getCanvas(), gameMap);
        try {
            enemiesConfig = loadEnemiesConfig("./enemy/enemies.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            wavesConfig = loadWavesConfig("./waves/waves.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        waveController = new WaveController(wavesConfig, enemiesConfig, gameMap.getEnemyPaths().size());
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
    }

    public void update() {
        if (!running) return;
        
        if(!waveController.updateWave(enemies, gameMap.getSpawnPoint(), 800/gameMap.getWidth(), 600 / gameMap.getHeight()) && enemies.isEmpty()){
            stop();
            return;
        }

        // Обновление врагов
        for (Enemy enemy : enemies) {
            enemy.move(gameMap); // Двигаем врагов по пути
            if (enemy.isDead()) {
                if (base != null) {
                    base.takeDamage(enemy.getDamageToBase()); // Наносим урон базе, если враг достиг её
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
        for (Tower tower : towers){
            towerView.renderTower(tower);
            if (tower.getAttackX() != -1) {
                towerView.renderBullets(tower, 5);
            }
        }
        allEnemiesView.renderEnemies(gc, enemies);
    }



}
