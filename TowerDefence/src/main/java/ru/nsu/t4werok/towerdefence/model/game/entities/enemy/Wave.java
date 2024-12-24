package ru.nsu.t4werok.towerdefence.model.game.entities.enemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Wave {
    private final List<Enemy> enemies; // Список врагов
    private final int interval; // Интервал появления врагов
    private boolean isRunning; // Флаг активности волны
    private int currentEnemyIndex; // Текущий индекс врага

    // Конструктор
    public Wave(List<Enemy> enemies, int interval) {
        this.enemies = new ArrayList<>(enemies);
        this.interval = interval;
        this.isRunning = false;
        this.currentEnemyIndex = 0;
    }

    // Старт волны
    public void startWave() {
        if (isRunning) {
            System.out.println("Wave is already running!");
            return;
        }
        isRunning = true;
        System.out.println("Wave started!");
        scheduleNextEnemy();
    }

    // Остановка волны
    public void stopWave() {
        isRunning = false;
        System.out.println("Wave stopped.");
    }

    // Проверка завершения волны
    public boolean isCompleted() {
        return currentEnemyIndex >= enemies.size();
    }

    // Запланировать появление следующего врага
    private void scheduleNextEnemy() {
        if (isCompleted()) {
            System.out.println("Wave completed!");
            isRunning = false;
            return;
        }

        Enemy enemy = enemies.get(currentEnemyIndex);
        spawnEnemy(enemy);
        currentEnemyIndex++;

        if (isRunning) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    scheduleNextEnemy();
                }
            }, interval);
        }
    }

    // Появление врага
    private void spawnEnemy(Enemy enemy) {
        System.out.println("Spawned enemy");

    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
}
