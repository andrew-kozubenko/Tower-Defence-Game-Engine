package ru.nsu.t4werok.towerdefence.controller.menu;

import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.network.server.GameServer;
import ru.nsu.t4werok.towerdefence.view.menu.MapSelectionView;

public class LobbyController {
    private final SceneController sceneController;
    private final GameServer gameServer;

    public LobbyController(SceneController sceneController, GameServer gameServer) {
        this.sceneController = sceneController;
        this.gameServer = gameServer;
    }

    public GameServer getGameServer() {
        return gameServer;
    }

    public String getIpAddress() {
        return gameServer.getIpAddress();
    }

    public int getPort() {
        return gameServer.getPort();
    }

    public void onStartGamePressed() {
        MapSelectionController mapController = new MapSelectionController(sceneController, true, gameServer);
        MapSelectionView mapView = new MapSelectionView(mapController);
        sceneController.addScene("MapSelectionMultiplayer", mapView.getScene());
        sceneController.switchTo("MapSelectionMultiplayer");
    }
}


