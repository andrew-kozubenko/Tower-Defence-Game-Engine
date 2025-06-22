package ru.nsu.t4werok.towerdefence.net;

import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.controller.menu.LobbyController;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Клиент кооператива: поддержка лобби и синхронизации игры.
 */
public class MultiplayerClient extends Thread implements NetworkSession {

    private static final String HELLO   = "HELLO";
    private static final String PLAYERS = "PLAYERS";
    private static final String START   = "START";

    private final String hostIp;
    private final int    port;
    private final String nickname;

    private Socket socket;
    private PrintWriter out;

    private volatile boolean running = true;
    private volatile GameController controller;
    private volatile LobbyController lobbyController;

    private final List<String> playerNames = new CopyOnWriteArrayList<>();

    public MultiplayerClient(String hostIp, int port, String nickname) {
        this.hostIp   = hostIp;
        this.port     = port;
        this.nickname = nickname;
        setDaemon(true);
        setName("TD-Client");
    }

    /* ---------------- life-cycle ---------------- */

    public void connect() throws IOException {
        socket = new Socket(hostIp, port);
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        out.println(HELLO + ";" + nickname);          // identify in lobby
        start();
    }

    public void disconnect() { close(); }

    public void setLobbyController(LobbyController l) { this.lobbyController = l; }
    public void attachGameController(GameController c){ this.controller = c; }

    public List<String> getConnectedPlayers() {
        return new ArrayList<>(playerNames);
    }

    /* ------------------- thread ------------------ */

    @Override
    public void run() {
        try (BufferedReader in =
                     new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String line;
            while (running && (line = in.readLine()) != null) {

                /* lobby */
                if (line.startsWith(PLAYERS)) {
                    String[] p = line.split(";");
                    playerNames.clear();
                    for (int i = 1; i < p.length; i++) playerNames.add(p[i]);
                    continue;
                }
                if (line.startsWith(START)) {
                    String[] p = line.split(";", 2);
                    String map = p.length > 1 ? p[1] : null;
                    if (lobbyController != null)
                        lobbyController.onGameStartSignal(map);
                    continue;
                }

                /* game */
                if (NetworkMessage.isPlaceTower(line) && controller != null) {
                    String[] p = NetworkMessage.split(line);
                    controller.placeTowerRemote(p[1],
                            Integer.parseInt(p[2]), Integer.parseInt(p[3]));
                }
            }
        } catch (IOException ignored) {
        } finally { close(); }
    }

    /* -------------- NetworkSession --------------- */

    @Override
    public void sendPlaceTower(String towerName, int x, int y) {
        if (out != null)
            out.println(NetworkMessage.encodePlaceTower(towerName, x, y));
    }

    @Override public boolean isConnected() { return socket != null && socket.isConnected() && !socket.isClosed(); }
    @Override public boolean isHost()      { return false; }

    @Override
    public void close() {
        running = false;
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }
}
