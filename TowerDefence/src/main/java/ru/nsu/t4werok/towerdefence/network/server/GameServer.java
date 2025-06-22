package ru.nsu.t4werok.towerdefence.network.server;

import ru.nsu.t4werok.towerdefence.model.game.entities.map.Base;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameServer {
    private int port;
    private String hostAddress; // <-- IP, на который можно подключиться

    private final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private boolean running = false;

    private GameMap gameMap;
    private Base base;

    private Runnable onReadyCallback;

    public GameServer() {
        // Порт и IP будут определены при запуске
    }

    public void start() {
        running = true;

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(0)) { // 0 = любой свободный порт
                this.port = serverSocket.getLocalPort();
                this.hostAddress = getLocalNonLoopbackAddress();

                System.out.println("Server started on " + hostAddress + ":" + port);

                if (onReadyCallback != null) {
                    onReadyCallback.run(); // ✅ уведомляем, что сервер готов
                }

                while (running) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());

                    ClientHandler handler = new ClientHandler(clientSocket, this);
                    clients.add(handler);
                    new Thread(handler).start();
                }
            } catch (IOException e) {
                System.err.println("Server error: " + e.getMessage());
            }
        }).start();
    }

    public void setOnReady(Runnable callback) {
        this.onReadyCallback = callback;
    }


    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void removeClient(ClientHandler handler) {
        clients.remove(handler);
    }

    public void stop() {
        running = false;
        System.out.println("Server shutting down...");
    }

    public int getPort() {
        return port;
    }

    public String getIpAddress() {
        return hostAddress;
    }

    public int getClientCount() {
        return clients.size();
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public Base getBase() {
        return base;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    // 🔍 Получение локального IP, не loopback
    private String getLocalNonLoopbackAddress() throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : java.util.Collections.list(nets)) {
            if (!netint.isUp() || netint.isLoopback()) continue;
            for (InetAddress inetAddress : java.util.Collections.list(netint.getInetAddresses())) {
                if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                    return inetAddress.getHostAddress();
                }
            }
        }
        return "127.0.0.1"; // fallback
    }
}
