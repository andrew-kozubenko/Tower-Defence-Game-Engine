package ru.nsu.t4werok.towerdefence.managers.game.entities;

import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;

import java.util.ArrayList;
import java.util.List;

public class WaveManager {
    private final List<Enemy> enemies = new ArrayList<>();
    private int waveNumber = 0;

    public void startNextWave() {
        waveNumber++;
        System.out.println("Starting wave " + waveNumber);
        for (int i = 0; i < waveNumber * 5; i++) {
//            enemies.add(new Enemy(100, 1.0)); // Пример: 100 HP, скорость 1.0
        }
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void update() {
//        enemies.removeIf(enemy -> enemy.isDead());
    }
}

