package ru.nsu.t4werok.towerdefence.view.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import ru.nsu.t4werok.towerdefence.controller.menu.LobbyController;

public class LobbyView {
    private final Scene scene;

    public LobbyView(LobbyController controller) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);

        String ip = controller.getIpAddress();
        int port = controller.getPort();

        Label waitingLabel = new Label("Waiting for players...");
        Label ipLabel = new Label("IP: " + ip);
        Label portLabel = new Label("Port: " + port);
        Button startGameButton = new Button("Start Game");

        startGameButton.setOnAction(e -> controller.onStartGamePressed());

        layout.getChildren().addAll(waitingLabel, ipLabel, portLabel, startGameButton);

        this.scene = new Scene(layout, 400, 300);
    }

    public Scene getScene() {
        return scene;
    }
}

