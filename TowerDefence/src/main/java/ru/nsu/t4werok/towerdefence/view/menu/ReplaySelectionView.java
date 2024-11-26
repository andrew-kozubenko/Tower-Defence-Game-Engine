package ru.nsu.t4werok.towerdefence.view.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import ru.nsu.t4werok.towerdefence.controller.menu.ReplaySelectionController;

import java.io.File;

public class ReplaySelectionView {
    private final Scene scene;

    public ReplaySelectionView(ReplaySelectionController controller) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);

        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> controller.onBackButtonPressed());

        Button playReplayButton = new Button("Play Replay");
        playReplayButton.setOnAction(e -> {
            File replayFile = controller.chooseReplayFile();
            controller.onPlayReplayButtonPressed(replayFile);
        });

        layout.getChildren().addAll(playReplayButton, backButton);
        this.scene = new Scene(layout, 400, 300);
    }

    public Scene getScene() {
        return scene;
    }
}
