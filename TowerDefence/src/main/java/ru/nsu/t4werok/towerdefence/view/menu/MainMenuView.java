// src/main/java/ru/nsu/t4werok/towerdefence/view/menu/MainMenuView.java
package ru.nsu.t4werok.towerdefence.view.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import ru.nsu.t4werok.towerdefence.controller.menu.MainMenuController;

public class MainMenuView {

    private final Scene scene;

    public MainMenuView(MainMenuController controller) {
        VBox root = new VBox(16);
        root.setAlignment(Pos.CENTER);

        Button playBtn = new Button("Play");
        playBtn.setOnAction(e -> controller.onPlayButtonPressed());

        Button mpBtn = new Button("Multiplayer");
        mpBtn.setOnAction(e -> controller.onMultiplayerButtonPressed());

        Button replayBtn = new Button("Replays");
        replayBtn.setOnAction(e -> controller.onReplayButtonPressed());

        Button settingsBtn = new Button("Settings");
        settingsBtn.setOnAction(e -> controller.onSettingsButtonPressed());

        Button exitBtn = new Button("Exit");
        exitBtn.setOnAction(e -> controller.onExitButtonPressed());

        root.getChildren().addAll(playBtn, mpBtn, replayBtn, settingsBtn, exitBtn);
        scene = new Scene(root, 800, 600);
    }

    public Scene getScene() {
        return scene;
    }
}
