package ru.nsu.t4werok.towerdefence.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.managers.menu.SettingsManager;

import java.util.HashMap;
import java.util.Map;

public class SceneController {
    private final Stage stage; // Главное окно приложения
    private final Map<String, Scene> scenes = new HashMap<>(); // Карта для хранения сцен
    private final SettingsManager settingsManager = SettingsManager.getInstance();

    public SceneController(Stage stage) {
        this.stage = stage;
    }

    /**
     * Добавить сцену в контроллер.
     *
     * @param name  Уникальное имя сцены.
     * @param scene Объект сцены.
     */
    public void addScene(String name, Scene scene) {
        scenes.put(name, scene);
    }

    /**
     * Переключиться на сцену с указанным именем.
     *
     * @param name Уникальное имя сцены.
     */
    public void switchTo(String name) {
        Scene scene = scenes.get(name);
        if (scene == null) {
            throw new IllegalArgumentException("Scene with name '" + name + "' does not exist.");
        }

//        // Получаем размеры из настроек
//        double width = settingsManager.getWidth();
//        double height = settingsManager.getHeight();
//
//        stage.setWidth(width);
//        stage.setHeight(height);

        stage.setScene(scene);

//        scene.getRoot().applyCss();
//        scene.getRoot().layout();
    }
    /**
     * Удалить сцену из контроллера.
     *
     * @param name Уникальное имя сцены.
     */
    public void removeScene(String name) {
        scenes.remove(name);
    }
}
