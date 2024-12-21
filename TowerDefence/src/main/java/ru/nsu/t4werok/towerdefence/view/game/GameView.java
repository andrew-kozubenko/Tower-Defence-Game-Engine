package ru.nsu.t4werok.towerdefence.view.game;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import ru.nsu.t4werok.towerdefence.app.GameEngine;

public class GameView {
    private final Scene scene;
    private final Canvas canvas; // Полотно для рисования

    public GameView(GameEngine gameEngine) {
        canvas = new Canvas(800, 600); // Размер окна игры
        GraphicsContext gc = canvas.getGraphicsContext2D(); // Контекст рисования

        // Контейнер для полотна
        StackPane layout = new StackPane(canvas);
        this.scene = new Scene(layout);

        // Добавляем обработчики событий (например, для управления)
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case P -> gameEngine.stop(); // Пример: пауза игры
                case Q -> System.exit(0);   // Выход из игры
            }
        });
    }

    public Scene getScene() {
        return scene;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
