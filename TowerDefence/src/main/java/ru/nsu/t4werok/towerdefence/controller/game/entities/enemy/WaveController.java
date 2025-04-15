package ru.nsu.t4werok.towerdefence.controller.game.entities.enemy;

import ru.nsu.t4werok.towerdefence.config.game.entities.enemy.EnemiesConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.enemy.EnemyConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.enemy.WavesConfig;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;

import java.util.List;

public class WaveController {
    private final EnemiesConfig enemiesConfig;
    private final WavesConfig wavesConfig;
    private final int allPaths;         // Количество возможных путей для спавна врагов

    private int numberOfWave = 0;       // Индекс текущей волны
    private int numberOfEnemy = 0;      // Счётчик врагов внутри текущей волны
    private int numberOfTicks = 0;      // Счётчик «тиков» между спавнами
    private int numberOfPath = 0;       // Индекс пути, по которому пойдёт очередной враг

    private boolean stopped = false;    // Все волны пройдены/игра остановлена
    private boolean waveActive = true;  // Текущая волна активна (идут спавны)

    public WaveController(WavesConfig wavesConfig, EnemiesConfig enemiesConfig, int allPaths) {
        this.wavesConfig = wavesConfig;
        this.enemiesConfig = enemiesConfig;
        this.allPaths = allPaths;
    }

    /**
     * Основной метод, вызываемый каждый кадр/тик.
     * Проверяет, нужно ли заспавнить врага, и следит за окончанием волны.
     *
     * @param enemies     список врагов на карте
     * @param spawnPoint  координаты спавна (в тайловых координатах)
     * @param cellSizeX   ширина клетки
     * @param cellSizeY   высота клетки
     * @return true, если игра продолжается, false если все волны закончились (или остановлены).
     */
    public boolean updateWave(List<Enemy> enemies, Integer[] spawnPoint, int cellSizeX, int cellSizeY) {
        if (stopped) {
            // Все волны закончились или игра остановлена
            return false;
        }

        // Если текущая волна не активна (т.е. закончилась), но ещё не вызван nextWave()
        if (!waveActive) {
            return true; // игра продолжается, но без спавна врагов
        }

        // Увеличиваем счётчик тиков
        numberOfTicks++;

        // Когда счётчик тиков достиг нужного интервала, спавним врага
        int interval = wavesConfig.getWaves()[numberOfWave].getInterval();
        if (numberOfTicks >= interval) {
            numberOfTicks = 0;

            // Спавним врага
            spawnEnemy(enemies, spawnPoint, cellSizeX, cellSizeY);

            // Увеличиваем счётчик врагов этой волны
            numberOfEnemy++;

            // Если заспавнили всех врагов текущей волны
            if (numberOfEnemy >= wavesConfig.getWaves()[numberOfWave].getEnemies().length) {
                // Волна закончена
                waveActive = false;
                System.out.println("Wave " + (numberOfWave + 1) + " completed. Waiting for nextWave() call...");

                // Если это была последняя волна - останавливаемся
                if (numberOfWave >= wavesConfig.getWaves().length - 1) {
                    stopped = true;
                    System.out.println("All waves completed");
                    return false;
                }
            }
        }
        // Волна ещё в процессе, игра продолжается
        return true;
    }

    /**
     * Запускает следующую волну, если предыдущая закончена.
     *
     * @return true, если волна успешно запущена,
     *         false, если волн больше нет или текущая ещё не закончилась.
     */
    public boolean nextWave() {
        if (stopped) {
            // Все волны уже закончились
            System.out.println("No more waves to start. The game is over or all waves are done.");
            return false;
        }
        if (waveActive) {
            // Текущая волна ещё не закончилась
            System.out.println("Cannot start next wave — wave " + (numberOfWave + 1) + " is still active.");
            return false;
        }

        // Переходим к следующей волне
        numberOfWave++;
        if (numberOfWave >= wavesConfig.getWaves().length) {
            // На всякий случай проверим, вдруг вышли за предел
            stopped = true;
            System.out.println("No more waves available. Game ended.");
            return false;
        }

        // Сбрасываем счётчики для новой волны
        numberOfEnemy = 0;
        numberOfTicks = 0;
        waveActive = true;

        System.out.println("Wave " + (numberOfWave + 1) + " started.");
        return true;
    }

    /**
     * Спавнит нового врага (Enemy) и добавляет его в список.
     */
    private void spawnEnemy(List<Enemy> enemies, Integer[] spawnPoint, int cellSizeX, int cellSizeY) {
        // Смотрим, какого врага спавнить (ID из массива волны - 1)
        int enemyId = wavesConfig.getWaves()[numberOfWave].getEnemies()[numberOfEnemy] - 1;

        EnemyConfig enemyConfig = enemiesConfig.getEnemiesConfigs()[enemyId];

        // Запоминаем текущий индекс пути, чтобы вывести в логи
        int currentPathIndex = numberOfPath;

        // Создаём врага
        Enemy enemy = new Enemy(
                enemyConfig.getLifePoints(),
                enemyConfig.getSpeed(),
                enemyConfig.getDamageToBase(),
                enemyConfig.getLoot(),
                spawnPoint[0] * cellSizeX,
                spawnPoint[1] * cellSizeY,
                currentPathIndex
        );

        // Инкрементируем pathIndex (чтобы следующий враг шёл по другому пути)
        numberOfPath++;
        if (numberOfPath >= allPaths) {
            numberOfPath = 0;
        }

        enemies.add(enemy);
        System.out.println("Spawned enemy: Wave " + (numberOfWave + 1)
                + ", Enemy " + (numberOfEnemy + 1)
                + ", Path " + currentPathIndex);
    }
}
