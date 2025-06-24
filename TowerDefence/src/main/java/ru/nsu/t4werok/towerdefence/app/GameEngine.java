package ru.nsu.t4werok.towerdefence.app;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
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
import ru.nsu.t4werok.towerdefence.net.LocalMultiplayerContext;
import ru.nsu.t4werok.towerdefence.net.MultiplayerServer;
import ru.nsu.t4werok.towerdefence.net.NetworkSession;
import ru.nsu.t4werok.towerdefence.net.protocol.NetMessage;
import ru.nsu.t4werok.towerdefence.net.protocol.NetMessageType;
import ru.nsu.t4werok.towerdefence.utils.ResourceManager;
import ru.nsu.t4werok.towerdefence.view.game.GameView;
import ru.nsu.t4werok.towerdefence.view.game.entities.enemy.AllEnemiesView;
import ru.nsu.t4werok.towerdefence.view.game.entities.tower.TowerView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ru.nsu.t4werok.towerdefence.utils.EnemiesLoader.loadEnemiesConfig;
import static ru.nsu.t4werok.towerdefence.utils.WavesLoader.loadWavesConfig;

public class GameEngine {
    /* =================== постоянные объекты =================== */
    private final GameMap            gameMap;
    private final SceneController    sceneController;
    private final SettingsManager    settingsManager = SettingsManager.getInstance();

    /* ---------- отрисовка ---------- */
    private final GameController  gameController;
    private final GameView        gameView;
    private final AllEnemiesView  allEnemiesView;
    private final TowerView       towerView;

    /* ---------- данные игры ---------- */
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Tower> towers  = new ArrayList<>();
    private final EnemiesConfig enemiesConfig;
    private final WavesConfig   wavesConfig;
    private final WaveController waveController;
    private final Base           base;

    /* ---------- сеть ---------- */
    private final NetworkSession session;      // может быть null, если игра одиночная
    private final boolean        iAmHost;      // true – если session instanceof MultiplayerServer

    /* ---------- сервисные поля ---------- */
    private static final double TARGET_FPS           = 30.0;
    private static final double TARGET_TIME_PER_FRAME= 1_000_000_000.0 / TARGET_FPS;

    private boolean        running       = false;
    private AnimationTimer gameLoop;
    private long           lastUpdateTime= 0;
    private int            prevBaseHp;          // для отправки BASE_HP только при изменении

    /* ==========================================================
                              КОНСТРУКТОР
       ========================================================== */
    public GameEngine(GameMap gameMap, SceneController sc, Base base) {

        /* ----------- базовые ссылки ----------- */
        this.gameMap         = gameMap;
        this.sceneController = sc;
        this.base            = base;

        /* ----------- UI / контроллеры ----------- */
        this.gameController  = new GameController(this, sc, gameMap, towers);
        this.gameView        = new GameView(gameController, this);
        allEnemiesView       = new AllEnemiesView();
        towerView            = new TowerView(gameController,
                gameView.getGc(),
                gameView.getCanvas(),
                gameMap);

        /* ----------- ресурсы ----------- */
        Path enemiesPath = ResourceManager.getEnemiesConfigFile();
        Path wavesPath   = ResourceManager.getWavesConfigFile();
        try {
            enemiesConfig = loadEnemiesConfig(enemiesPath.toString());
            wavesConfig   = loadWavesConfig  (wavesPath.toString());
        } catch (IOException e) {
            throw new RuntimeException("Can't load configs", e);
        }

        /* ----------- сеть ----------- */
        session  = LocalMultiplayerContext.get().getSession();
        iAmHost  = session != null && session.isHost();

        /* ----------- WaveController с коллбэком для хоста ----------- */
        waveController = new WaveController(
                wavesConfig,
                enemiesConfig,
                gameMap.getEnemyPaths().size(),
                (w, e, p) -> {                   // коллбэк хоста
                    if (iAmHost && session instanceof MultiplayerServer srv) {
                        srv.sendEnemySpawn(w, e, p);
                    }
                });

        /* ----------- сцена ---------- */
        sc.addScene("Game", gameView.getScene());
        sc.switchTo("Game");

        LocalMultiplayerContext.get().bindEngine(this);
        prevBaseHp = base.getHealth();
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setRunning(boolean running) {
        this.running = running;
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
        gameView.deleteButtonNextWave();
    }

    public void update() {
        if (!running) return;

        // Обновляем текущую волну. Если волны кончились и врагов нет - останавливаем игру.
        if (!waveController.updateWave(enemies, gameMap.getSpawnPoint(),
                800 / gameMap.getWidth(), 600 / gameMap.getHeight()) && enemies.isEmpty()) {
            stop();
            return;
        }

        // Обновление врагов
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            enemy.move(gameMap); // Двигаем врагов по пути
            if (enemy.isDead()) {
                if (base != null) {
                    base.takeDamage(enemy.getDamageToBase()); // Наносим урон базе, если враг достиг её
                    enemies.remove(enemy);
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

        System.out.println("Hello");
        gameView.getMapView().renderMap();
        for (Tower tower : towers) {
            towerView.renderTower(tower);
            if (tower.getAttackX() != -1) {
                towerView.renderBullets(tower, 5);
                tower.setAttackX(-1);
                tower.setAttackY(-1);
            }
        }

        // Отрисовываем врагов
        allEnemiesView.renderEnemies(gc, enemies);

        // Отрисовка прямоугольника с информацией (деньги и здоровье)
        double rectX = canvas.getWidth() - 150; // Позиция прямоугольника
        double rectY = 0;
        double rectWidth = 130;
        double rectHeight = 40;

        gc.setFill(Color.WHITE);
        gc.fillRect(rectX, rectY, rectWidth, rectHeight);

        gc.setStroke(Color.BLACK);
        gc.strokeRect(rectX, rectY, rectWidth, rectHeight);

        String coinsText = gameController.coinsNow() + "$ " + base.getHealth() + "HP";
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(16));
        gc.fillText(coinsText, rectX + 10, rectY + 25);
    }

    /**
     * Кнопка Next-Wave на стороне хоста.
     * Клиентам эту кнопку нажимать не нужно — они получат WAVE_START.
     */
    public boolean nextWave() {
        if (iAmHost){
            int idx = waveController.nextWaveHost();
            if (idx < 0) return false;

            long seed = new Random().nextLong();
            if (session instanceof MultiplayerServer srv)
                srv.sendWaveStart(idx, seed);

            return true;
        }
        /* клиент: локальная кнопка разрешена лишь для офф-лайна */
        return waveController.nextWave();
    }

    /* ==========================================================
                        ОБРАБОТКА СЕТИ
       ========================================================== */
    public void handleNetworkMessage(NetMessage msg){
        switch (msg.getType()) {

            /* ---------- башни ---------- */
            case PLACE_TOWER -> {
                gameController.placeTowerRemote(msg.get("tower"),
                        (Integer)msg.get("x"),
                        (Integer)msg.get("y"));
            }

            /* ---------- старт волны ---------- */
            case WAVE_START -> {
                int  idx  = (Integer) msg.get("idx");
                long seed = ((Number) msg.get("seed")).longValue();
                waveController.forceStart(idx, seed);
            }

            /* ---------- появление конкретного врага ---------- */
            case ENEMY_SPAWN -> {
                int wi = (Integer) msg.get("wave");
                int ei = (Integer) msg.get("enemy");
                int pi = (Integer) msg.get("path");

                waveController.remoteSpawn(
                        wi, ei, pi,
                        enemies,
                        gameMap.getSpawnPoint(),
                        800 / gameMap.getWidth(),
                        600 / gameMap.getHeight());
            }

            /* ---------- здоровье базы ---------- */
            case BASE_HP -> {
                int hp = (Integer) msg.get("hp");
                base.setHealth(hp);
                prevBaseHp = hp;
            }

            default -> {}
        }
    }
}
