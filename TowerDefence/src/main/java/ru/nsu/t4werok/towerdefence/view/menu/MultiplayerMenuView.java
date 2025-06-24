package ru.nsu.t4werok.towerdefence.view.menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.nsu.t4werok.towerdefence.controller.menu.MultiplayerMenuController;

/**
 * Multiplayer-меню: слева список карт, справа Host / Join формы.
 * Передаёт port и nickname в методы контроллера.
 */
public class MultiplayerMenuView {

    private final Scene scene;

    public MultiplayerMenuView(MultiplayerMenuController c) {

        /* ---------- список карт ---------- */
        ListView<String> mapsList = new ListView<>(c.getMaps());
        mapsList.setPrefWidth(220);
        mapsList.getSelectionModel().selectedItemProperty()
                .addListener((obs, o, n) -> c.selectMap(n));

        /* ---------- HOST блок ---------- */
        TextField hostPort   = new TextField("8080");
        TextField hostNick   = new TextField("Player");
        Button    hostBtn    = new Button("Create lobby");
        hostBtn.setOnAction(e ->
                c.onHostClicked(Integer.parseInt(hostPort.getText()), hostNick.getText()));

        VBox hostBox = new VBox(8,
                new Label("Host game"),
                new Label("Port:"), hostPort,
                new Label("Nickname:"), hostNick,
                hostBtn);
        hostBox.setPadding(new Insets(10));
        hostBox.setAlignment(Pos.TOP_LEFT);

        /* ---------- JOIN блок ---------- */
        TextField joinIp     = new TextField("127.0.0.1");
        TextField joinPort   = new TextField("8080");
        TextField joinNick   = new TextField("Player");
        Button    joinBtn    = new Button("Join");
        joinBtn.setOnAction(e ->
                c.onJoinClicked(joinIp.getText(),
                        Integer.parseInt(joinPort.getText()),
                        joinNick.getText()));

        VBox joinBox = new VBox(8,
                new Label("Join game"),
                new Label("Host IP:"), joinIp,
                new Label("Port:"), joinPort,
                new Label("Nickname:"), joinNick,
                joinBtn);
        joinBox.setPadding(new Insets(10));
        joinBox.setAlignment(Pos.TOP_LEFT);

        /* ---------- правая панель ---------- */
        VBox right = new VBox(20, hostBox, new Separator(), joinBox);
        right.setPadding(new Insets(10));

        /* ---------- нижняя навигация ---------- */
        Button back = new Button("Back");
        back.setOnAction(e -> c.onBackClicked());
        HBox bottom = new HBox(back);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(10));

        /* ---------- корневой layout ---------- */
        BorderPane root = new BorderPane();
        root.setLeft(mapsList);
        root.setCenter(right);
        root.setBottom(bottom);
        BorderPane.setMargin(mapsList, new Insets(10));
        BorderPane.setMargin(right, new Insets(10));

        scene = new Scene(root, 800, 500);
    }

    public Scene getScene() { return scene; }
}
