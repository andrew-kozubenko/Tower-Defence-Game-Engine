package ru.nsu.t4werok.towerdefence.controller.menu;

import ru.nsu.t4werok.towerdefence.app.GameEngine;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.Base;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.network.client.GameClient;

import java.io.IOException;
import java.util.function.BiConsumer;

public class JoinServerController {
    private final SceneController sceneController;
    private GameClient gameClient;

    public JoinServerController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    public void connectToServer(String ip, int port,
                                BiConsumer<GameMap, Base> onGameStart,
                                Runnable onWaiting) throws IOException {
        this.gameClient = new GameClient(ip, port);

        if (gameClient.isConnected()) {
            // показать "ожидание"
            onWaiting.run();

            // передаем коллбэк старта
            gameClient.setOnGameStart((map, base) -> {
                new GameEngine(map, sceneController, base, gameClient);
                onGameStart.accept(map, base); // для UI
            });
        } else {
            throw new IOException("Connection failed.");
        }
    }
}
