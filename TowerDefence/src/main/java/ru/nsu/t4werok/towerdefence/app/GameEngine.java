package ru.nsu.t4werok.towerdefence.app;

import javafx.animation.AnimationTimer;
import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerConfig;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.controller.game.entities.tower.TowerController;
import ru.nsu.t4werok.towerdefence.controller.menu.SettingsController;
import ru.nsu.t4werok.towerdefence.managers.menu.SettingsManager;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.Base;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.view.game.GameView;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private final GameMap gameMap; // Карта, на которой происходит игра
    private boolean running = false; // Флаг состояния игры
    private AnimationTimer gameLoop; // Игровой цикл
    private final List<Enemy> enemies = new ArrayList<>(); // Список врагов
    private final List<Tower> towers = new ArrayList<>(); // Список башен на карте

    private int waveNumber = 0; // Номер текущей волны

    private final GameController gameController;
    private final GameView gameView;
    private final SceneController sceneController;
    private final SettingsManager settingsManager = SettingsManager.getInstance();

    public void setRunning(boolean running) {
        this.running = running;
    }

    private Base base; // База

    public GameEngine(GameMap gameMap, SceneController sceneController, Base base) {
        this.gameMap = gameMap;
        this.sceneController = sceneController;
        this.base = base; // Передаем базу
        this.gameController = new GameController(this, sceneController, gameMap, towers);
        this.gameView = new GameView(gameController);
        // Добавляем игровую сцену в SceneController
        sceneController.addScene("Game", gameView.getScene());
        // Переключаемся на игровую сцену
        sceneController.switchTo("Game");
    }

    public void start() {
        running = true;
        settingsManager.setRunningGame(true);


        // Игровой цикл
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!running) {
                    settingsManager.setRunningGame(false);
                    stop();
                    return;
                }
                update(); // Обновление состояния игры
                render(); // Отрисовка объектов
            }
        };
        gameLoop.start();
    }

    public void stop() {
        running = false;
    }

    public void update() {
        if (!running) return;

        // Обновление врагов
        for (Enemy enemy : enemies) {
            enemy.move(gameMap.getEnemyPaths().get(0)); // Двигаем врагов по пути
            if (enemy.isDead()) {
                if (base != null) {
                    base.takeDamage(enemy.getDamageToBase()); // Наносим урон базе, если враг достиг её
                }
            }
        }

        // Обновление башен
        for (Tower tower : towers) {
            // Логика атаки башни на врагов
        }

        // Удаление мертвых врагов
        enemies.removeIf(Enemy::isDead);

        // Проверка поражения
        if (base.getHealth() <= 0) {
            stop();
            System.out.println("Game Over!");
        }
    }

    private void render() {
        // Логика отрисовки объектов на карте
        // System.out.println("Rendering game scene...");
    }

    public void startNextWave() {
        waveNumber++;
        System.out.println("Starting wave " + waveNumber);

        // Генерация врагов для новой волны
        for (int i = 0; i < waveNumber * 5; i++) {
            // Здесь создаём врага
            Enemy enemy = new Enemy(100 + waveNumber * 10, 1, 5, 10, 50, new ArrayList<>(), 0, 3);
            enemies.add(enemy);
        }
    }

}
