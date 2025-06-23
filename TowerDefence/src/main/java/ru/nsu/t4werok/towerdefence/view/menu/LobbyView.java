// src/main/java/ru/nsu/t4werok/towerdefence/view/menu/LobbyView.java
package ru.nsu.t4werok.towerdefence.view.menu;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import ru.nsu.t4werok.towerdefence.controller.menu.LobbyController;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple lobby: shows connected players, “Start” (host only) and “Leave” buttons.
 */
public class LobbyView {

    private final Scene scene;
    private final Timer refresher = new Timer(true);

    public LobbyView(LobbyController controller) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        ListView<String> players = new ListView<>();
        root.setCenter(players);

        Button leaveBtn = new Button("Leave");
        leaveBtn.setOnAction(e -> {
            refresher.cancel();
            controller.leaveLobby();
        });

        Button startBtn = new Button("Start");
        startBtn.setDisable(!controller.isHost());
        startBtn.setOnAction(e -> controller.onStartClicked());

        BorderPane bottom = new BorderPane();
        bottom.setLeft(leaveBtn);
        bottom.setRight(startBtn);
        bottom.setPadding(new Insets(10));
        root.setBottom(bottom);

        /* periodic refresh of player list */
        refresher.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() ->
                        players.getItems().setAll(controller.getPlayerNames()));
            }
        }, 0, 1000);

        scene = new Scene(root, 400, 300);
    }

    public Scene getScene() {
        return scene;
    }
}
