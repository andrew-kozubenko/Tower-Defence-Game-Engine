package ru.nsu.t4werok.towerdefence.view.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.controller.menu.MainMenuController;

public class MainMenuView {
    private final Scene scene;

    public MainMenuView(MainMenuController controller) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);

        Button playButton = new Button("Play");
        playButton.setOnAction(e -> controller.onPlayButtonPressed());

        Button replayButton = new Button("Replays");
        replayButton.setOnAction(e -> controller.onReplayButtonPressed());

        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(e -> controller.onSettingsButtonPressed());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> controller.onExitButtonPressed());

        layout.getChildren().addAll(playButton, replayButton, settingsButton, exitButton);
        this.scene = new Scene(layout, 400, 300);
    }

    public Scene getScene() {
        return scene;
    }
}
