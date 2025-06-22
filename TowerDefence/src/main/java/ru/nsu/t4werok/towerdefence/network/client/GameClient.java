package ru.nsu.t4werok.towerdefence.network.client;

import ru.nsu.t4werok.towerdefence.model.game.entities.map.Base;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;

import java.io.*;
import java.net.Socket;
import java.util.function.BiConsumer;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected = false;

    private GameMap receivedMap;
    private Base receivedBase;

    private BiConsumer<GameMap, Base> onGameStart; // Коллбэк при старте игры

    public GameClient(String serverIp, int port) throws IOException {
        connect(serverIp, port);
    }

    public void connect(String serverIp, int port) throws IOException {
        socket = new Socket(serverIp, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        connected = true;

        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    handleServerMessage(line);
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server.");
            } finally {
                connected = false;
            }
        }).start();

        System.out.println("Connected to server at " + serverIp + ":" + port);
    }

    public void sendMessage(String message) {
        if (out != null && connected) {
            out.println(message);
        }
    }

    public void setOnGameStart(BiConsumer<GameMap, Base> handler) {
        this.onGameStart = handler;
    }

    private void handleServerMessage(String message) {
        // Простейший протокол: получаем карту и базу по отдельности
        if (message.startsWith("START_GAME")) {
            System.out.println("Received START_GAME from server");

            try {
                ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
                receivedMap = (GameMap) objIn.readObject();
                receivedBase = (Base) objIn.readObject();

                if (onGameStart != null) {
                    onGameStart.accept(receivedMap, receivedBase);
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("[Server] " + message);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void disconnect() throws IOException {
        connected = false;
        if (socket != null) socket.close();
    }
}
