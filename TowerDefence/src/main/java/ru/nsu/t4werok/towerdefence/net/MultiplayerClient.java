package ru.nsu.t4werok.towerdefence.net;

import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.controller.menu.LobbyController;
import ru.nsu.t4werok.towerdefence.net.protocol.NetMessage;
import ru.nsu.t4werok.towerdefence.net.protocol.NetMessageType;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Клиент кооператива: поддержка лобби и синхронизации игры.
 * Логика прежняя, вместо «CMD;…» передаются/принимаются JSON-NetMessage.
 */
public class MultiplayerClient extends Thread implements NetworkSession {

    private static final Path USER_SD_DIR =
            Paths.get(System.getProperty("user.home"),
                    "Documents", "Games", "TowerDefenceSD");

    private final String hostIp;
    private final int    port;
    private final String nickname;

    private Socket socket;
    private PrintWriter out;

    private volatile boolean       running         = true;
    private volatile GameController   controller;
    private volatile LobbyController lobbyController;
    private volatile SceneController sceneController;

    private final List<String> playerNames = new CopyOnWriteArrayList<>();

    public MultiplayerClient(String hostIp, int port, String nickname) {
        this.hostIp   = hostIp;
        this.port     = port;
        this.nickname = nickname;
        setDaemon(true);
        setName("TD-Client");
    }

    /* ---------- DI ---------- */
    public void injectSceneController(SceneController sc) { this.sceneController = sc; }

    /* ---------------- life-cycle ---------------- */

    public void connect() throws IOException {
        socket = new Socket(hostIp, port);
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        /* HELLO */
        send(new NetMessage(NetMessageType.HELLO, Map.of("name", nickname)));

        start();
    }

    public void disconnect() { close(); }
    public void setLobbyController(LobbyController l) { this.lobbyController = l; }
    public void attachGameController(GameController c){ this.controller      = c; }
    public List<String> getConnectedPlayers() { return new ArrayList<>(playerNames); }

    /* ------------------- thread ------------------ */

    @Override
    public void run() {
        try (BufferedReader in =
                     new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String line;
            while (running && (line = in.readLine()) != null) {
                NetMessage msg = NetMessage.fromJson(line);

                switch (msg.getType()) {

                    /* ---------- lobby ---------- */
                    case PLAYERS -> {
                        // payload: { players : ["nick1","nick2",…] }
                        List<?> list = msg.get("players");
                        playerNames.clear();
                        for (Object o : list) playerNames.add(String.valueOf(o));
                    }

                    case START -> handleStart(msg);

                    /* ---------- game ----------- */
                    case PLACE_TOWER -> {
                        if (controller == null) break;
                        String tower = msg.get("tower");
                        int    x     = (Integer) msg.get("x");
                        int    y     = (Integer) msg.get("y");
                        controller.placeTowerRemote(tower, x, y);
                    }

                    default -> { /* игнорируем остальные */ }
                }
            }
        } catch (IOException ignored) {
        } finally { close(); }
    }

    /* ---------------- handle START ---------------- */
    private void handleStart(NetMessage msg) {
        String map  = msg.get("map");
        String data = msg.get("data");                 // base64-zip

        try {
            byte[] zipBytes = Base64.getDecoder().decode(data);
            unpackZipToSd(zipBytes);

            /* 👉 Сначала сообщаем лобби (если оно ещё на экране) */
            if (lobbyController != null) {
                lobbyController.onGameStartSignal(map);
            } else if (sceneController != null) {
                /* fallback – когда лобби уже закрыто/не установлено */
                sceneController.startMultiplayerGame(Paths.get(map), /*isHost=*/false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unpackZipToSd(byte[] zipBytes) throws IOException {
        if (!Files.exists(USER_SD_DIR)) Files.createDirectories(USER_SD_DIR);
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry e;
            while ((e = zis.getNextEntry()) != null) {
                Path dst = USER_SD_DIR.resolve(e.getName());
                Files.createDirectories(dst.getParent());
                try (OutputStream out = Files.newOutputStream(dst,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING)) {
                    zis.transferTo(out);
                }
            }
        }
    }

    /* -------------- NetworkSession --------------- */

    @Override
    public void sendPlaceTower(String towerName, int x, int y) {
        send(new NetMessage(NetMessageType.PLACE_TOWER,
                Map.of("tower", towerName, "x", x, "y", y)));
    }

    private void send(NetMessage msg) {
        if (out != null) out.println(msg.toJson());
    }

    @Override public boolean isConnected() { return socket != null && socket.isConnected() && !socket.isClosed(); }
    @Override public boolean isHost()      { return false; }

    @Override
    public void close() {
        running = false;
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }
}
