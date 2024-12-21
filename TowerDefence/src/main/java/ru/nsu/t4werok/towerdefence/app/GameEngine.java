package ru.nsu.t4werok.towerdefence.app;

import javafx.animation.AnimationTimer;
import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.controller.game.entities.tower.TowerController;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private final GameMap gameMap; // Карта, на которой происходит игра
    private boolean running = false; // Флаг состояния игры
    private AnimationTimer gameLoop; // Игровой цикл
    private final List<Enemy> enemies = new ArrayList<>(); // Список врагов
    private final List<Tower> towers = new ArrayList<>(); // Список башен на карте
    private List<Tower> towersForSelect; // Список башен на выбор
    private Tower selectedTower;

    private int waveNumber = 0; // Номер текущей волны

    private final GameController gameController;

    public GameEngine(GameMap gameMap) {
        this.gameMap = gameMap;
        this.gameController = new GameController(gameMap, towers);
    }

    public void start() {
        running = true;

        this.towersForSelect = gameController.loadTowerFromConfigs("towers");

        // Игровой цикл
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!running) {
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
//            enemy.move(); // Двигаем врагов
//            if (enemy.reachedBase(map.getBase())) {
//                map.getBase().damage(enemy.getDamage());
//            }
        }

        // Обновление башен
        for (Tower tower : towers) {
//            tower.attack(enemies);
        }

        // Удаление мертвых врагов
        enemies.removeIf(Enemy::isDead);

        // Проверка поражения
        if (gameMap.getBase().getHealth() <= 0) {
            stop();
            System.out.println("Game Over!");
        }
    }

    private void render() {
        // Логика отрисовки объектов на карте
        System.out.println("Rendering game scene...");
    }

    public void startNextWave() {
        waveNumber++;
        System.out.println("Starting wave " + waveNumber);
        // Генерация врагов для новой волны
        for (int i = 0; i < waveNumber * 5; i++) {
//            enemies.add(new Enemy(100 + waveNumber * 10, 1.0));
        }
    }

    public void addTower() {
        gameController.placeTower(selectedTower);
    }
}
