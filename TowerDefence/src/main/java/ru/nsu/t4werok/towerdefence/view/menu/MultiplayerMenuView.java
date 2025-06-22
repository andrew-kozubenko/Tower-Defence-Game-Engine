package ru.nsu.t4werok.towerdefence.view.menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.controller.menu.MultiplayerMenuController;

public class MultiplayerMenuView {
    private final Scene scene;

    public MultiplayerMenuView(MultiplayerMenuController controller) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Multiplayer");
        Button createServerButton = new Button("Create Server");
        Button joinServerButton = new Button("Join Server");
        Button backButton = new Button("Back");

        createServerButton.setOnAction(e -> controller.onCreateServerPressed());
        joinServerButton.setOnAction(e -> controller.onJoinServerPressed());
        backButton.setOnAction(e -> controller.onBackPressed());

        layout.getChildren().addAll(titleLabel, createServerButton, joinServerButton, backButton);

        this.scene = new Scene(layout, 400, 300);
    }

    public Scene getScene() {
        return scene;
    }
}

