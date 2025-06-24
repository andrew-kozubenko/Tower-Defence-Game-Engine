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
import ru.nsu.t4werok.towerdefence.net.MultiplayerClient;
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
import java.util.Map;
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

    /* -------- новые константы и поля -------- */
    private static final int  SYNC_EVERY_TICKS = 15;          // хост → клиенты
    private int  tickCounter = 0;                             // NEW
    private MultiplayerServer srv;                            // NEW

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
        session = LocalMultiplayerContext.get().getSession();
        iAmHost = session!=null && session.isHost();
        System.out.println(iAmHost);
        if(iAmHost) srv = (MultiplayerServer) session;

        /* ----------- WaveController с коллбэком для хоста ----------- */
        waveController = new WaveController(
                wavesConfig,
                enemiesConfig,
                gameMap.getEnemyPaths().size(),
                null);

        /* ----------- сцена ---------- */
        sc.addScene("Game", gameView.getScene());
        sc.switchTo("Game");

        LocalMultiplayerContext.get().bindEngine(this);
        prevBaseHp = base.getHealth();

        start();
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

        gameLoop = new AnimationTimer() {
            @Override public void handle(long now){
                if(!running){ settingsManager.setRunningGame(false); stop(); return; }
                if(now-lastUpdateTime >= TARGET_TIME_PER_FRAME){
                    lastUpdateTime = now;
                    update();
                    render();
                }
            }
        };
        gameLoop.start();
    }

    public void stop() {
        running = false;
        gameView.deleteButtonNextWave();
    }

    /* =================== ОБНОВЛЕНИЕ =================== */
    public void update() {

        /* --------- обновление волн --------- */
        boolean cont;
        if (iAmHost) {
            cont = waveController.updateWaveHost(enemies,
                    gameMap.getSpawnPoint(),
                    800 / gameMap.getWidth(),
                    600 / gameMap.getHeight());
        } else {
            cont = waveController.updateWave(enemies,
                    gameMap.getSpawnPoint(),
                    800 / gameMap.getWidth(),
                    600 / gameMap.getHeight());
        }
        if (!cont && enemies.isEmpty()) { stop(); return; }

        /* --------- движение врагов и урон базе --------- */
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            e.move(gameMap);
            if (e.isDead()) {
                base.takeDamage(e.getDamageToBase());
                enemies.remove(i--);
            }
        }

        /* ---------- синхронизация HP базы ---------- */
        if(iAmHost){
            if(++tickCounter>=SYNC_EVERY_TICKS){
                tickCounter=0;
                srv.sendStateSync(base.getHealth(),enemies,waveController.getCurrentWaveIndex());
            }
        }

        /* ---------- башни ---------- */
        gameController.updateTower(enemies);

        if (base.getHealth() <= 0) { stop(); System.out.println("Game Over!"); }
    }

    private void render() {
        GraphicsContext gc = gameView.getGc();
        Canvas canvas = gameView.getCanvas();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

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
        if (session == null) {
            // Одиночная игра
            return waveController.nextWave();
        }

        if (iAmHost) {
            int idx = waveController.nextWaveHost();
            if (idx < 0) return false;
            long seed = new Random().nextLong();
            srv.sendWaveSync(idx, seed);
            return true;
        }

        // Клиент: отправляем запрос хосту
        if (session instanceof MultiplayerClient cli) {
            cli.requestNextWave();
        }
        return false;
    }


    /* ==========================================================
                        ОБРАБОТКА СЕТИ
       ========================================================== */
    /* =================== ОБРАБОТКА СЕТИ =================== */
    public void handleNetworkMessage(NetMessage msg) {
        switch (msg.getType()) {

            case PLACE_TOWER -> gameController.placeTowerRemote(
                    msg.get("tower"), (Integer) msg.get("x"), (Integer) msg.get("y"));

            case WAVE_SYNC -> {
                if(!running) start();
                waveController.forceStart(msg.get("idx"),((Number)msg.get("seed")).longValue());
            }
            case STATE_SYNC -> {
//                /* полная замена списка врагов и HP (клиент) */
//                base.setHealth((Integer) msg.get("hp"));
//                enemies.clear();
//                List<?> arr = msg.get("data");
//                for(Object o:arr){
//                    Map<?,?> m=(Map<?,?>)o;
//                    enemies.add(new Enemy(
//                            (Integer)m.get("hp"),0,0,0,
//                            (Integer)m.get("x"),(Integer)m.get("y"),
//                            (Integer)m.get("path")));
//                }
            }

            default -> {
            }
        }
    }
}
