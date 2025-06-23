package ru.nsu.t4werok.towerdefence.net;

import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.net.protocol.NetMessage;
import ru.nsu.t4werok.towerdefence.net.protocol.NetMessageType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Хост-сервер: лобби + ретрансляция игровых событий.
 * Формат обмена — NetMessage(JSON). Семантика старого протокола не изменена.
 *
 *  – HELLO      {name}                регистрация в лобби
 *  – PLAYERS    {players:[…]}         актуальный список игроков
 *  – START      {map}                 сигнал начала игры
 *  – PLACE_TOWER{tower,x,y}           действие в игре
 */
public class MultiplayerServer extends Thread implements NetworkSession {

    private static final Path USER_SD_DIR =
            Paths.get(System.getProperty("user.home"),
                    "Documents", "Games", "TowerDefenceSD");

    private final int  port;
    private final String mapPath;           // карта, выбранная хостом
    private final GameController controller;// null, пока лобби
    private SceneController sceneController;// внедряется из LobbyController

    private final List<ClientHandler> clients     = new CopyOnWriteArrayList<>();
    private final List<String>        playerNames = new CopyOnWriteArrayList<>();

    private final ServerSocket serverSocket;
    private volatile boolean   running = true;

    /* --------------------------- ctor ---------------------------- */

    public MultiplayerServer(int port, String mapPath) throws IOException {
        this(port, null, mapPath);
    }
    public MultiplayerServer(int port,
                             GameController controller,
                             String mapPath) throws IOException {
        this.port       = port;
        this.controller = controller;
        this.mapPath    = mapPath;
        this.serverSocket = new ServerSocket(port);   // открываем порт сразу
        setDaemon(true);
        setName("TD-Server");
    }

    public void injectSceneController(SceneController sc) { this.sceneController = sc; }

    /* ---------------------------- run ---------------------------- */

    @Override
    public void run() {
        try {
            while (running) {
                Socket s = serverSocket.accept();
                clients.add(new ClientHandler(s));
            }
        } catch (IOException ignored) {
        } finally { close(); }
    }

    /** Вызывается из LobbyController, когда хост нажал «Start». */
    public void startGame() {
        try {
            // 1. Сформировать zip-архив всех нужных ресурсов
            byte[] zipBytes = buildGameAssetsZip();

            // 2. Base64-строка для пересылки
            String blob = Base64.getEncoder().encodeToString(zipBytes);

            // 3. Broadcast START {map, data}
            NetMessage startMsg = new NetMessage(NetMessageType.START,
                    Map.of("map",  mapPath,
                            "data", blob));
            broadcast(startMsg);

            // 4. Распаковать локально (хосту тоже нужен SD каталог)
            unpackZipToSd(zipBytes);

            // 5. Запустить движок локально
            if (sceneController != null)
                sceneController.startMultiplayerGame(Paths.get(mapPath), /*isHost=*/true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ---------- helpers ---------- */

    private byte[] buildGameAssetsZip() throws IOException {
        // Собираем директории, которые надо отдать клиенту
        Path root = Paths.get("").toAbsolutePath(); // корень проекта (resources рядом)
        List<Path> dirs = List.of(
                root.resolve("maps"),
                root.resolve("towers"),
                root.resolve("waves"),
                root.resolve("enemy"),
                root.resolve("settings"),
                root.resolve("techTree"),
                root.resolve("image")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Path dir : dirs) {
                if (!Files.exists(dir)) continue;
                Files.walk(dir).forEach(p -> {
                    if (Files.isDirectory(p)) return;
                    try (InputStream in = Files.newInputStream(p)) {
                        String entryName = root.relativize(p).toString().replace("\\", "/");
                        zos.putNextEntry(new ZipEntry(entryName));
                        in.transferTo(zos);
                        zos.closeEntry();
                    } catch (IOException e) { throw new UncheckedIOException(e); }
                });
            }
            // карта отдельно (если лежит вне каталога maps — добавим явно)
            Path map = Paths.get(mapPath);
            if (Files.exists(map)) {
                try (InputStream in = Files.newInputStream(map)) {
                    zos.putNextEntry(new ZipEntry("maps/" + map.getFileName()));
                    in.transferTo(zos);
                    zos.closeEntry();
                }
            }
        }
        return baos.toByteArray();
    }

    private void unpackZipToSd(byte[] zipBytes) throws IOException {
        if (!Files.exists(USER_SD_DIR)) Files.createDirectories(USER_SD_DIR);
        try (java.util.zip.ZipInputStream zis =
                     new java.util.zip.ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry e;
            while ((e = zis.getNextEntry()) != null) {
                Path dst = USER_SD_DIR.resolve(e.getName());
                Files.createDirectories(dst.getParent());
                try (OutputStream out = Files.newOutputStream(dst, StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING)) {
                    zis.transferTo(out);
                }
            }
        }
    }

    /* -------------------- NetworkSession ------------------------ */

    @Override
    public void sendPlaceTower(String towerName, int x, int y) {
        if (controller != null)
            controller.placeTowerRemote(towerName, x, y);

        NetMessage msg = new NetMessage(NetMessageType.PLACE_TOWER,
                Map.of("tower", towerName,
                        "x", x, "y", y));
        broadcast(msg);
    }

    @Override public boolean isConnected() { return running && !serverSocket.isClosed(); }
    @Override public boolean isHost()      { return true; }

    @Override
    public void close() {
        running = false;
        try { serverSocket.close(); } catch (IOException ignored) {}
        clients.forEach(ClientHandler::close);
        clients.clear();
        playerNames.clear();
    }

    /* --------------------- lobby helpers ------------------------ */

    private void sendPlayersToAll() {
        broadcast(new NetMessage(NetMessageType.PLAYERS,
                Map.of("players", playerNames)));
    }

    /* ------------------- broadcast util ------------------------- */

    private void broadcast(NetMessage msg) {
        clients.forEach(c -> c.send(msg));
    }

    /* ======================  ClientHandler  ===================== */

    private class ClientHandler extends Thread {

        private final Socket socket;
        private final BufferedReader in;
        private final PrintWriter    out;
        private String nickname = "Unknown";

        ClientHandler(Socket s) throws IOException {
            this.socket = s;
            this.in  = new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
            setDaemon(true);
            start();
        }

        /* -------- net tx -------- */
        void send(NetMessage msg) { out.println(msg.toJson()); }

        /* -------- main loop ----- */
        @Override
        public void run() {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    NetMessage msg = NetMessage.fromJson(line);

                    switch (msg.getType()) {

                        /* -------- lobby -------- */
                        case HELLO -> {
                            nickname = msg.get("name");
                            playerNames.add(nickname);
                            sendPlayersToAll();
                        }

                        /* -------- game --------- */
                        case PLACE_TOWER -> {
                            String tower = msg.get("tower");
                            int    x     = (Integer) msg.get("x");
                            int    y     = (Integer) msg.get("y");

                            if (controller != null)
                                controller.placeTowerRemote(tower, x, y);

                            /* пересылаем остальным клиентам */
                            NetMessage relay = new NetMessage(NetMessageType.PLACE_TOWER,
                                    Map.of("tower", tower,
                                            "x", x, "y", y));
                            clients.stream()
                                    .filter(c -> c != this)
                                    .forEach(c -> c.send(relay));
                        }

                        default -> { /* игнор */ }
                    }
                }
            } catch (IOException ignored) {
            } finally {
                playerNames.remove(nickname);
                sendPlayersToAll();
                close();
                clients.remove(this);
            }
        }

        void close() { try { socket.close(); } catch (IOException ignored) {} }
    }
}
