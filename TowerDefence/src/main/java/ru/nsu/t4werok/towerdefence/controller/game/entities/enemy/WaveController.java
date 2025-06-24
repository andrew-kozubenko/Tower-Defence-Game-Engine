package ru.nsu.t4werok.towerdefence.controller.game.entities.enemy;

import ru.nsu.t4werok.towerdefence.config.game.entities.enemy.EnemiesConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.enemy.EnemyConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.enemy.WavesConfig;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;

import java.util.List;
import java.util.Random;

/**
 * Управляет логикой волн и — если мы хост — информирует коллбэком о каждом
 * заспавненном враге.
 */
public class WaveController {

    private final EnemiesConfig enemiesConfig;
    private final WavesConfig   wavesConfig;
    private final int           allPaths;

    private int  numberOfWave  = -1;
    private int  numberOfEnemy = 0;
    private int  numberOfTicks = 0;
    private int  numberOfPath  = 0;

    private boolean stopped     = false;
    private boolean waveActive  = false;   // <-- первая волна ждёт сигнала!
    private Random  rng         = new Random(0);

    /* коллбэк хоста */
    private final SpawnCallback spawnCallback;

    public WaveController(WavesConfig wavesCfg,
                          EnemiesConfig enemiesCfg,
                          int allPaths,
                          SpawnCallback cb) {
        this.wavesConfig     = wavesCfg;
        this.enemiesConfig   = enemiesCfg;
        this.allPaths        = allPaths;
        this.spawnCallback   = cb;
    }

    /* ------------------------------------------------------------------ */
    /*                    ОБНОВЛЕНИЕ (одиночка/клиент)                    */
    /* ------------------------------------------------------------------ */
    public boolean updateWave(List<Enemy> enemies,
                              Integer[] spawnPoint,
                              int cellSizeX, int cellSizeY) {

        if (stopped || !waveActive) return !stopped;

        numberOfTicks++;

        int interval = wavesConfig.getWaves()[numberOfWave].getInterval();
        if (numberOfTicks >= interval) {
            numberOfTicks = 0;
            spawnEnemy(enemies, spawnPoint, cellSizeX, cellSizeY);
            numberOfEnemy++;

            if (numberOfEnemy >= wavesConfig.getWaves()[numberOfWave].getEnemies().length) {
                waveActive = false;
                if (numberOfWave >= wavesConfig.getWaves().length - 1) stopped = true;
            }
        }
        return true;
    }

    /* ------------------------------------------------------------------ */
    /*                       ОБНОВЛЕНИЕ ДЛЯ ХОСТА                         */
    /* ------------------------------------------------------------------ */
    public boolean updateWaveHost(List<Enemy> enemies,
                                  Integer[] spawnPoint,
                                  int cellSizeX, int cellSizeY) {

        int before = enemies.size();
        boolean cont = updateWave(enemies, spawnPoint, cellSizeX, cellSizeY);

        /* если появился новый враг — шлём коллбэку */
        if (spawnCallback != null && enemies.size() > before) {
            Enemy e = enemies.get(enemies.size() - 1);
            spawnCallback.onSpawn(numberOfWave, numberOfEnemy - 1, e.getCurrentPathIndex());
        }
        return cont;
    }

    /* ------------------------------------------------------------------ */
    /*                       СТАРТ/ФОРС-СТАРТ ВОЛНЫ                       */
    /* ------------------------------------------------------------------ */
    /* ------------------------------------------------------------------ */
    /*                ЛОКАЛЬНЫЙ СТАРТ (single-player / клиент)            */
    /* ------------------------------------------------------------------ */

    /**
     * Запустить следующую волну локально (без синхронизации по сети).
     * Используется:
     *   • одиночная игра;
     *   • клиент, если игра без подключённого хоста.
     *
     * @return true – если волна стартовала, false – если нельзя.
     */
    public boolean nextWave() {
        return internalStartNextWave(false) >= 0;
    }

    /* ------------------------------------------------------------------ */
    /*                       ХОСТОВСКИЙ СТАРТ ВОЛНЫ                       */
    /* ------------------------------------------------------------------ */
    public int nextWaveHost() {
        return internalStartNextWave(true);          // true → вернём индекс
    }

    /* ------------------------------------------------------------------ */
    /*                   ОБЩАЯ РЕАЛИЗАЦИЯ ПЕРЕКЛЮЧЕНИЯ                     */
    /* ------------------------------------------------------------------ */
    private int internalStartNextWave(boolean returnIdx) {

        if (waveActive || stopped) return returnIdx ? -1 :  false?1:-1; // -1 / false

        numberOfWave++;
        if (numberOfWave >= wavesConfig.getWaves().length) { stopped = true; return returnIdx ? -1 : false?1:-1; }

        numberOfEnemy = numberOfTicks = numberOfPath = 0;
        waveActive    = true;
        return returnIdx ? numberOfWave : 0;  // 0 значит «ok» для boolean-ветки
    }

    public void forceStart(int idx, long seed) {
        if (idx < 0 || idx >= wavesConfig.getWaves().length) return;

        numberOfWave  = idx;
        numberOfEnemy = numberOfTicks = numberOfPath = 0;
        waveActive    = true;
        stopped       = false;
        rng           = new Random(seed);
    }

    /* ------------------------------------------------------------------ */
    /*                         КЛИЕНТСКИЙ SPAWN                           */
    /* ------------------------------------------------------------------ */
    public void remoteSpawn(int waveIdx, int enemyIdx, int pathIdx,
                            List<Enemy> enemies,
                            Integer[] spawnPoint,
                            int cellSizeX, int cellSizeY) {

        if (waveIdx != numberOfWave || enemyIdx != numberOfEnemy) return;

        numberOfPath = (pathIdx + 1) % allPaths;

        int id = wavesConfig.getWaves()[waveIdx].getEnemies()[enemyIdx] - 1;
        EnemyConfig cfg = enemiesConfig.getEnemiesConfigs()[id];

        enemies.add(new Enemy(cfg.getLifePoints(), cfg.getSpeed(),
                cfg.getDamageToBase(), cfg.getLoot(),
                spawnPoint[0] * cellSizeX,
                spawnPoint[1] * cellSizeY,
                pathIdx));

        numberOfEnemy++;
        if (numberOfEnemy >= wavesConfig.getWaves()[waveIdx].getEnemies().length)
            waveActive = false;
    }

    /* ------------------------------------------------------------------ */
    /*                         PRIVATE HELPERS                            */
    /* ------------------------------------------------------------------ */
    private void spawnEnemy(List<Enemy> enemies,
                            Integer[] spawnPoint,
                            int cellSizeX, int cellSizeY) {

        int id = wavesConfig.getWaves()[numberOfWave].getEnemies()[numberOfEnemy] - 1;
        EnemyConfig cfg = enemiesConfig.getEnemiesConfigs()[id];

        enemies.add(new Enemy(cfg.getLifePoints(), cfg.getSpeed(),
                cfg.getDamageToBase(), cfg.getLoot(),
                spawnPoint[0] * cellSizeX,
                spawnPoint[1] * cellSizeY,
                numberOfPath));

        numberOfPath = (numberOfPath + 1) % allPaths;
    }

    /* ------------------------------------------------------------------ */
    public interface SpawnCallback {
        void onSpawn(int waveIdx, int enemyIdx, int pathIdx);
    }
}
