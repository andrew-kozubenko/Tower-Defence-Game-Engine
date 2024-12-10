package ru.nsu.t4werok.towerdefence.view.menu;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import ru.nsu.t4werok.towerdefence.controller.menu.SettingsController;

public class SettingsView {
    private final SettingsController controller;
    private final Scene scene;

    public SettingsView(SettingsController controller) {
        this.controller = controller;

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Громкость
        Label volumeLabel = new Label("Volume:");
        Slider volumeSlider = new Slider(0, 100, 50);

        // Разрешение
        Label resolutionLabel = new Label("Resolution:");
        ComboBox<String> resolutionComboBox = new ComboBox<>();
        resolutionComboBox.getItems().addAll("800x600", "1280x720", "1920x1080", "2560x1440");
        resolutionComboBox.setValue("800x600");

        // Кнопка Apply
        Button applyButton = new Button("Apply");
        applyButton.setOnAction(e -> {
            controller.setVolume((int) volumeSlider.getValue()); // Передаем числовое значение громкости
            controller.setResolution(resolutionComboBox.getValue()); // Передаем строку разрешения
            controller.applySettings();
        });


        // Кнопка "Back" для возврата в главное меню
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> controller.onBackButtonPressed());

        root.getChildren().addAll(
                volumeLabel, volumeSlider,
                resolutionLabel, resolutionComboBox,
                applyButton, backButton
        );

        // Инициализация контроллера
        controller.initialize(volumeSlider, resolutionComboBox);

        this.scene = new Scene(root, 400, 300);
    }

    public Scene getScene() {
        return scene;
    }
}

