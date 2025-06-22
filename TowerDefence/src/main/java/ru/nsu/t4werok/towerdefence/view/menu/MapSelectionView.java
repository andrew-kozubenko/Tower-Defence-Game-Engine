/* src/main/java/ru/nsu/t4werok/towerdefence/view/menu/MapSelectionView.java */
package ru.nsu.t4werok.towerdefence.view.menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import ru.nsu.t4werok.towerdefence.controller.menu.MapSelectionController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Список карт + 2 кнопки: «Play» (solo) и «Multiplayer».
 */
public class MapSelectionView {

    private final Scene scene;

    public MapSelectionView(MapSelectionController controller) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        /* ---------- список карт ---------- */
        ListView<Path> list = new ListView<>();
        try {
            list.getItems().setAll(
                    Files.list(Paths.get("assets/maps"))
                            .filter(p -> p.toString().endsWith(".json"))
                            .collect(Collectors.toList()));
        } catch (Exception ignored) {}
        list.getSelectionModel().selectedItemProperty()
                .addListener((obs, o, n) -> controller.onMapChosen(n));
        root.setCenter(list);

        /* ---------- кнопки ---------- */
        Button playBtn = new Button("Play");
        playBtn.setOnAction(e -> controller.onPlaySingle());

        Button mpBtn = new Button("Multiplayer");
        mpBtn.setOnAction(e -> controller.onPlayMultiplayer());

        HBox buttons = new HBox(10, playBtn, mpBtn);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10));
        root.setBottom(buttons);

        scene = new Scene(root, 600, 400);
    }

    public Scene getScene() { return scene; }
}
