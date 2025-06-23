package ru.nsu.t4werok.towerdefence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.app.GameEngine;
import ru.nsu.t4werok.towerdefence.config.game.entities.map.MapConfig;
import ru.nsu.t4werok.towerdefence.controller.menu.LobbyController;
import ru.nsu.t4werok.towerdefence.controller.menu.MultiplayerMenuController;
import ru.nsu.t4werok.towerdefence.managers.menu.SettingsManager;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.net.MultiplayerClient;
import ru.nsu.t4werok.towerdefence.net.MultiplayerServer;
import ru.nsu.t4werok.towerdefence.view.menu.LobbyView;
import ru.nsu.t4werok.towerdefence.view.menu.MultiplayerMenuView;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/** Управляет сценами и упрощает навигацию в кооперативе. */
public class SceneController {

    private final Stage stage;
    private final Map<String, Scene> scenes = new HashMap<>();
    private final SettingsManager settingsManager = SettingsManager.getInstance();

    public SceneController(Stage stage) { this.stage = stage; }

    /* ---------- базовый API ---------- */

    public void addScene(String n, Scene s) { scenes.put(n, s); }
    public boolean hasScene(String n)       { return scenes.containsKey(n); }
    public void removeScene(String n)       { scenes.remove(n); }

    public void switchTo(String n) {
        Runnable r = () -> {
            Scene s = scenes.get(n);
            if (s == null) throw new IllegalArgumentException("Scene '" + n + "' not found");
            stage.setScene(s);
        };
        if (Platform.isFxApplicationThread()) r.run(); else Platform.runLater(r);
    }

    /* =================  кооператив  ================= */

    public void showMultiplayerMenu(String preselect) {
        if (!hasScene("MultiplayerMenu")) {
            MultiplayerMenuController c = new MultiplayerMenuController(this);
            if (preselect != null) c.setSelectedMap(preselect);
            MultiplayerMenuView v = new MultiplayerMenuView(c);
            addScene("MultiplayerMenu", v.getScene());
        }
        switchTo("MultiplayerMenu");
    }

    public void openLobbyAsHost(int port, String nick, String mapPath) {
        try {
            MultiplayerServer srv = new MultiplayerServer(port, mapPath);
            srv.start();

            MultiplayerClient cli = new MultiplayerClient("127.0.0.1", port, nick);
            cli.connect();

            showLobby(srv, cli, true, mapPath);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void openLobbyAsClient(String ip, int port, String nick) {
        try {
            MultiplayerClient cli = new MultiplayerClient(ip, port, nick);
            cli.connect();

            showLobby(null, cli, false, null);           // карту сообщит сервер
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void showLobby(MultiplayerServer srv,
                          MultiplayerClient cli,
                          boolean isHost,
                          String mapPath) {
        LobbyController c = new LobbyController(this, srv, cli, isHost, mapPath);
        LobbyView v = new LobbyView(c);
        addScene("Lobby", v.getScene());
        switchTo("Lobby");
    }

    /* ======================================================================
       Новый метод: реальный запуск движка после получения START
       ====================================================================== */

    /**
     * Запускает игровой движок у клиента/хоста после сообщения START.
     *
     * @param mapPath путь к JSON-файлу карты (может быть относительным к SD-каталогу)
     * @param isHost  true – если мы являемся хостом (пока не нужен, но кладём для будущего)
     */
    public void startMultiplayerGame(Path mapPath, boolean isHost) {
        System.out.printf("[SceneController] Game start: map=%s host=%b%n",
                mapPath, isHost);

        /* 1. Приводим путь карты к абсолютному: ищем в ~/Documents/Games/TowerDefenceSD */
        Path sdRoot = Paths.get(System.getProperty("user.home"),
                "Documents", "Games", "TowerDefenceSD");
        Path fullMapPath = mapPath.isAbsolute()
                ? mapPath
                : sdRoot.resolve(mapPath).normalize();

        if (!Files.exists(fullMapPath)) {
            System.err.println("Map file not found: " + fullMapPath);
            switchTo("MainMenu");
            return;
        }

        /* 2. Читаем MapConfig из JSON */
        MapConfig cfg;
        try {
            cfg = new ObjectMapper().readValue(fullMapPath.toFile(), MapConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
            switchTo("MainMenu");
            return;
        }

        /* 3. Строим GameMap так же, как в одиночном режиме */
        GameMap gameMap = new GameMap(
                cfg.getWidth(),
                cfg.getHeight(),
                cfg.getEnemyPaths(),
                cfg.getTowerPositions(),
                cfg.getSpawnPoint(),
                cfg.getBase(),
                cfg.getBackgroundImagePath(),
                cfg.getBaseImagePath(),
                cfg.getTowerImagePath(),
                cfg.getSpawnPointImagePath()
        );

        /* 4. Создаём и запускаем GameEngine */
        GameEngine engine = new GameEngine(gameMap, this, gameMap.getBase());
        engine.start();

        /* 5. ⬅️  Новое: если мы в роли клиента, «скрестим» его с контроллером */
        var sess = ru.nsu.t4werok.towerdefence.net.LocalMultiplayerContext.get().getSession();
        if (sess instanceof ru.nsu.t4werok.towerdefence.net.MultiplayerClient cli) {
            cli.attachGameController(engine.getGameController());
        }
    }
}
