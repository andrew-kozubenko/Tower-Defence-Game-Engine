package ru.nsu.t4werok.towerdefence.view.menu;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import ru.nsu.t4werok.towerdefence.controller.menu.JoinServerController;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.Base;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;

import java.io.IOException;

public class JoinServerView {
    private final Scene scene;
    private final Label statusLabel = new Label(); // Для ожидания
    private final VBox layout;

    public JoinServerView(JoinServerController controller) {
        layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Label ipLabel = new Label("Server IP:");
        TextField ipField = new TextField("127.0.0.1");

        Label portLabel = new Label("Port:");
        TextField portField = new TextField("7777");

        Button connectButton = new Button("Connect");

        connectButton.setOnAction(e -> {
            String ip = ipField.getText();
            int port;
            try {
                port = Integer.parseInt(portField.getText());
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid port number.");
                return;
            }

            statusLabel.setText("Connecting to server...");
            try {
                controller.connectToServer(ip, port, this::onGameStart, this::showWaitingStatus);
            } catch (IOException ex) {
                statusLabel.setText("Connection failed: " + ex.getMessage());
            }
        });

        layout.getChildren().addAll(ipLabel, ipField, portLabel, portField, connectButton, statusLabel);
        this.scene = new Scene(layout, 400, 300);
    }

    public Scene getScene() {
        return scene;
    }

    public void showWaitingStatus() {
        Platform.runLater(() -> statusLabel.setText("Connected. Waiting for host to start the game..."));
    }

    private void onGameStart(GameMap map, Base base) {
        Platform.runLater(() -> statusLabel.setText("Game starting..."));
        // Здесь можно закрыть меню или передать в контроллер команду запустить игру
        // Например, можно вызвать controller.startGame(map, base);
    }
}
