package ru.nsu.t4werok.towerdefence.controller.menu;

import javafx.application.Platform;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.network.server.GameServer;
import ru.nsu.t4werok.towerdefence.view.menu.JoinServerView;
import ru.nsu.t4werok.towerdefence.view.menu.LobbyView;

public class MultiplayerMenuController {
    private final SceneController sceneController;

    public MultiplayerMenuController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    public void onCreateServerPressed() {
        GameServer server = new GameServer();
        server.start();

        // Ждём, пока порт и IP не инициализируются
        new Thread(() -> {
            while (server.getIpAddress() == null || server.getPort() == 0) {
                try {
                    Thread.sleep(50); // ждать 50мс
                } catch (InterruptedException ignored) {}
            }

            Platform.runLater(() -> {
                LobbyController lobbyController = new LobbyController(sceneController, server);
                LobbyView view = new LobbyView(lobbyController);
                sceneController.addScene("Lobby", view.getScene());
                sceneController.switchTo("Lobby");
            });
        }).start();
    }


    public void onJoinServerPressed() {
        JoinServerView view = new JoinServerView(new JoinServerController(sceneController));
        sceneController.addScene("JoinServer", view.getScene());
        sceneController.switchTo("JoinServer");
    }

    public void onBackPressed() {
        sceneController.switchTo("MainMenu");
    }
}
