package ru.nsu.t4werok.towerdefence.controller.game.entities.enemy;

import ru.nsu.t4werok.towerdefence.config.game.entities.enemy.EnemiesConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.enemy.EnemyConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.enemy.WavesConfig;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;

import java.util.List;

public class WaveController {
    private final EnemiesConfig enemiesConfig;
    private final WavesConfig wavesConfig;
    private final int allPaths;

    private int numberOfWave = 0;
    private int numberOfEnemy = 0;
    private int numberOfTicks = 0;
    private int numberOfPath = 0;

    private boolean stopped = false;
    private boolean waveActive = true;

    public WaveController(WavesConfig wavesConfig, EnemiesConfig enemiesConfig, int allPaths) {
        this.wavesConfig = wavesConfig;
        this.enemiesConfig = enemiesConfig;
        this.allPaths = allPaths;
    }

    public boolean updateWave(List<Enemy> enemies, Integer[] spawnPoint, int cellSizeX, int cellSizeY) {
        if (stopped) {
            return false;
        }
        if (!waveActive){
            return true;
        }

        numberOfTicks++;

        if (wavesConfig.getWaves()[numberOfWave].getInterval() == numberOfTicks) {
            numberOfTicks = 0;
            numberOfEnemy++;

            if (wavesConfig.getWaves()[numberOfWave].getEnemies().length == numberOfEnemy) {
                if (numberOfWave >= wavesConfig.getWaves().length) {
                    stopped = true;
                    System.out.println("All waves completed");
                    return false;
                }
                numberOfEnemy = 0;
                waveActive = false; // Останавливаем волну до вызова nextWave()
                System.out.println("StopWave");
            }

            spawnEnemy(enemies, spawnPoint, cellSizeX, cellSizeY);
        }

        return true;
    }

    public boolean nextWave() {
        if (stopped) {
            return false;
        }
        numberOfWave+=1;

        waveActive = true;
        System.out.println("Wave " + (numberOfWave + 1) + " started");

        if(numberOfWave >= wavesConfig.getWaves().length - 1){
            return true;
        }

        return false;
    }

    public void spawnEnemy(List<Enemy> enemies, Integer[] spawnPoint, int cellSizeX, int cellSizeY) {
        EnemyConfig enemyConfig = enemiesConfig
                .getEnemiesConfigs()[wavesConfig
                .getWaves()[numberOfWave]
                .getEnemies()[numberOfEnemy] - 1];

        Enemy enemy = new Enemy(
                enemyConfig.getLifePoints(),
                enemyConfig.getSpeed(),
                enemyConfig.getDamageToBase(),
                enemyConfig.getLoot(),
                spawnPoint[0] * cellSizeX,
                spawnPoint[1] * cellSizeY,
                numberOfPath
        );

        numberOfPath++;
        if (numberOfPath == allPaths) {
            numberOfPath = 0;
        }

        enemies.add(enemy);
        System.out.println("Spawned enemy");
    }
}
