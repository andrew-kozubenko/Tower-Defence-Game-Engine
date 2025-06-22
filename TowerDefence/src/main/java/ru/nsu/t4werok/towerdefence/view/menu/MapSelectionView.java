package ru.nsu.t4werok.towerdefence.view.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ru.nsu.t4werok.towerdefence.config.game.entities.map.MapConfig;
import ru.nsu.t4werok.towerdefence.controller.menu.MapSelectionController;

import java.util.List;


public class MapSelectionView {
    private final Scene scene;

    public MapSelectionView(MapSelectionController controller) {
        VBox layout = new VBox(15);  // Основной контейнер для отображения карт
        layout.setAlignment(Pos.CENTER);

        // Заголовок
        Label titleLabel = new Label("Select a GameMap:");

        // Контейнер для кнопок карт
        VBox mapsContainer = new VBox(10);
        mapsContainer.setAlignment(Pos.CENTER);

        // Загружаем карты через контроллер
        List<MapConfig> maps = controller.getAvailableMaps();

        if (maps.isEmpty()) {
            Label noMapsLabel = new Label("No Maps Available");
            mapsContainer.getChildren().add(noMapsLabel);
        } else {
            for (MapConfig mapConfig : maps) {
                String mapLabel = String.format("Map: " + mapConfig.getMapName(), mapConfig.getWidth(), mapConfig.getHeight());
                Button mapChoiceButton = new Button(mapLabel);
                mapChoiceButton.setOnAction(e -> controller.onMapSelected(mapConfig));
                mapsContainer.getChildren().add(mapChoiceButton);
            }
        }

        // Кнопка для возврата в главное меню
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> controller.onBackButtonPressed());

        // Добавляем все элементы в основной контейнер
        layout.getChildren().addAll(titleLabel, mapsContainer, backButton);

        // Создаём сцену с контейнером
        this.scene = new Scene(layout, 400, 300);

        layout.prefWidthProperty().bind(this.scene.widthProperty());
        layout.prefHeightProperty().bind(this.scene.heightProperty());
    }

    public Scene getScene() {
        return scene;
    }

//    public void show() {
//    }
}

