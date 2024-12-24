package ru.nsu.t4werok.towerdefence.controller.game.entities.enemy;

import ru.nsu.t4werok.towerdefence.config.game.entities.enemy.EnemiesConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.enemy.EnemyConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.enemy.WavesConfig;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;

import java.util.List;

public class WaveController {
    private final EnemiesConfig enemiesConfig;
    private int numberOfWave = 0;
    private int numberOfEnemy = 0;
    private int numberOfTicks = 0;
    private final WavesConfig wavesConfig;
    private int numberOfPath = 0;
    private final int allPaths;

    private boolean stopped = false;

    public WaveController(WavesConfig wavesConfig, EnemiesConfig enemiesConfig, int allPaths) {
        this.wavesConfig = wavesConfig;
        this.enemiesConfig = enemiesConfig;
        this.allPaths = allPaths;
    }

    public boolean updateWave(List<Enemy> enemies, Integer[] spawnPoint, int cellSizeX, int cellSizeY){
        if(stopped){
            return false;
        }
        numberOfTicks++;
        if (wavesConfig.getWaves()[numberOfWave].
                getInterval() == numberOfTicks){
            numberOfTicks = 0;
            numberOfEnemy++;
            if(wavesConfig.getWaves()[numberOfWave].
                    getEnemies().length == numberOfEnemy){
                numberOfEnemy = 0;
                numberOfWave++;
                if(wavesConfig.getWaves().length == numberOfWave){
                    System.out.println("FALSE\nFALSE\nFALSE\nFALSE\nFALSE\nFALSE\nFALSE\nFALSE\nFALSE\nFALSE");
                    stopped = true;
                    return false;
                }
            }
            spawnEnemy(enemies, spawnPoint, cellSizeX, cellSizeY);
        }
        return true;
    }

    public void spawnEnemy(List<Enemy> enemies, Integer[] spawnPoint, int cellSizeX, int cellSizeY){
        EnemyConfig enemyConfig = enemiesConfig.
                getEnemiesConfigs()[wavesConfig.
                getWaves()[numberOfWave].
                getEnemies()[wavesConfig.
                    getWaves()[numberOfWave].
                    getEnemies()[numberOfEnemy]] - 1];
        Enemy enemy = new Enemy(enemyConfig.getLifePoints(),
                enemyConfig.getSpeed(),
                enemyConfig.getDamageToBase(),
                enemyConfig.getLoot(),
                spawnPoint[0] * cellSizeX,
                spawnPoint[1] * cellSizeY,
                numberOfPath);
        numberOfPath++;
        if (numberOfPath == allPaths){
            numberOfPath = 0;
        }
        enemies.add(enemy);
        System.out.println("Spawned enemy");
    }
}
