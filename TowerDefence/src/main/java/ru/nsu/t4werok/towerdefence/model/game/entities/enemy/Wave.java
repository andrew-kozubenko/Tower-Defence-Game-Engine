package ru.nsu.t4werok.towerdefence.model.game.entities.enemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Wave {
    private final List<Enemy> enemies; // Список врагов в волне
    private final int interval; // Интервал появления врагов (в миллисекундах)
    private boolean isRunning; // Флаг активности волны
    private int currentEnemyIndex; // Текущий индекс врага в списке

    // Конструктор
    public Wave(List<Enemy> enemies, int interval) {
        this.enemies = new ArrayList<>(enemies); // Копируем список врагов
        this.interval = interval;
        this.isRunning = false;
        this.currentEnemyIndex = 0;
    }

    // Метод запуска волны
    public void startWave() {
        if (isRunning) {
            System.out.println("Wave is already running!");
            return;
        }
        isRunning = true;
        System.out.println("Wave started!");
        scheduleNextEnemy();
    }

    // Метод для остановки волны
    public void stopWave() {
        isRunning = false;
        System.out.println("Wave stopped.");
    }

    // Проверка завершения волны
    public boolean isCompleted() {
        return currentEnemyIndex >= enemies.size();
    }

    // Планирование появления следующего врага
    private void scheduleNextEnemy() {
        if (isCompleted()) {
            System.out.println("Wave completed!");
            isRunning = false;
            return;
        }

        // Эмулируем появление врага
        Enemy enemy = enemies.get(currentEnemyIndex);
        spawnEnemy(enemy);
        currentEnemyIndex++;

        if (isRunning) {
            // Запускаем следующий враг через заданный интервал
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    scheduleNextEnemy();
                }
            }, interval);
        }
    }

    // Метод для появления врага на карте
    private void spawnEnemy(Enemy enemy) {
        System.out.println("Spawned enemy: " + enemy.getModel());
        // Здесь можно добавить логику для добавления врага на карту
    }

    // Получение текущего состояния волны
    public String getWaveStatus() {
        if (isRunning) {
            return "Wave in progress: " + currentEnemyIndex + "/" + enemies.size() + " enemies spawned.";
        } else if (isCompleted()) {
            return "Wave completed!";
        } else {
            return "Wave not started.";
        }
    }
}

