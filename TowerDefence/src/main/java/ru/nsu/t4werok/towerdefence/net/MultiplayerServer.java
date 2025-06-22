package ru.nsu.t4werok.towerdefence.net;

import ru.nsu.t4werok.towerdefence.controller.game.GameController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Хост-сервер.
 * <pre>
 *  – HELLO   (клиент → сервер)          регистрация в лобби
 *  – PLAYERS    (сервер → клиенты)        актуальный список игроков
 *  – START    (сервер → клиенты)        сигнал начала игры
 *  – PLACE_TOWER                           во время игры
 * </pre>
 */
public class MultiplayerServer extends Thread implements NetworkSession {

    private static final String HELLO   = "HELLO";
    private static final String PLAYERS = "PLAYERS";
    private static final String START   = "START";

    private final int port;
    private final String mapPath;                    // карта, выбранная хостом
    private final GameController controller;         // null в лобби

    private final List<ClientHandler> clients     = new CopyOnWriteArrayList<>();
    private final List<String>        playerNames = new CopyOnWriteArrayList<>();

    private final ServerSocket serverSocket;
    private volatile boolean running = true;

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
        this.serverSocket = new ServerSocket(port);   // сразу открываем порт
        setDaemon(true);
        setName("TD-Server");
    }

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

    /* -------------------- NetworkSession ------------------------ */

    @Override
    public void sendPlaceTower(String towerName, int x, int y) {
        if (controller != null)
            controller.placeTowerRemote(towerName, x, y);
        broadcast(NetworkMessage.encodePlaceTower(towerName, x, y));
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
        broadcast(PLAYERS + ";" + String.join(";", playerNames));
    }

    public void startGame() {                        // вызов из LobbyController
        broadcast(START + ";" + mapPath);
    }

    public void shutdown() { close(); }

    private void broadcast(String msg) { clients.forEach(c -> c.send(msg)); }

    /* ======================  ClientHandler  ===================== */

    private class ClientHandler extends Thread {
        private final Socket socket;
        private final BufferedReader in;
        private final PrintWriter out;
        private String nickname = "Unknown";

        ClientHandler(Socket s) throws IOException {
            this.socket = s;
            this.in  = new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
            setDaemon(true);
            start();
        }

        void send(String msg) { out.println(msg); }

        @Override
        public void run() {
            try {
                String line;
                while ((line = in.readLine()) != null) {

                    /* -------- lobby -------- */
                    if (line.startsWith(HELLO)) {
                        nickname = line.split(";", 2)[1];
                        playerNames.add(nickname);
                        sendPlayersToAll();
                        continue;
                    }

                    /* -------- game --------- */
                    if (NetworkMessage.isPlaceTower(line)) {
                        String[] p = NetworkMessage.split(line);
                        if (controller != null)
                            controller.placeTowerRemote(p[1],
                                    Integer.parseInt(p[2]), Integer.parseInt(p[3]));
                        final String msg = line;
                        clients.stream().filter(c -> c != this).forEach(c -> c.send(msg));
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
