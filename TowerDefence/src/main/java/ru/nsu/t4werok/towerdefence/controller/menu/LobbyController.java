package ru.nsu.t4werok.towerdefence.controller.menu;

import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.net.MultiplayerClient;
import ru.nsu.t4werok.towerdefence.net.MultiplayerServer;

import java.nio.file.Paths;
import java.util.List;

/** Контроллер лобби. */
public class LobbyController {

    private final SceneController   sceneController;
    private final MultiplayerServer server;   // null, если клиент
    private final MultiplayerClient client;
    private final boolean isHost;
    private String mapPath;                   // может прийти от сервера

    public LobbyController(SceneController sc,
                           MultiplayerServer srv,
                           MultiplayerClient cli,
                           boolean isHost,
                           String mapPath) {
        this.sceneController = sc;
        this.server   = srv;
        this.client   = cli;
        this.isHost   = isHost;
        this.mapPath  = mapPath;

        client.setLobbyController(this);
    }

    /* ---------- данные для UI ---------- */
    public boolean isHost() { return isHost; }
    public List<String> getPlayerNames() { return client.getConnectedPlayers(); }

    /* ---------- кнопка *Start* (Host) ---------- */
    /** Метод должен быть указан в FXML как onAction="#onStartClicked". */
    public void onStartClicked() {
        if (isHost && server != null) server.startGame();
    }

    /* ---------- выход ---------- */
    public void leaveLobby() {
        client.disconnect();
        if (server != null) server.close();
        sceneController.switchTo("MainMenu");
        sceneController.removeScene("Lobby");
    }

    /* ---------- пришёл NetMessage START ---------- */
    public void onGameStartSignal(String mapFromHost) {
        if (mapPath == null && mapFromHost != null) mapPath = mapFromHost;
        sceneController.startMultiplayerGame(Paths.get(mapPath), isHost);
    }
}
