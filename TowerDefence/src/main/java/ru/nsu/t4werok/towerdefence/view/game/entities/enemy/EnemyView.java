package ru.nsu.t4werok.towerdefence.view.game.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;

public class EnemyView {
    private final Enemy enemy; // Ссылка на модель врага

    public EnemyView(Enemy enemy) {
        this.enemy = enemy;
    }

    // Метод для отображения врага
    public void render(GraphicsContext gc) {
        int radius = 10; // Радиус кружочка

        // Координаты врага
        int x = enemy.getX();
        int y = enemy.getY();

        // Устанавливаем цвет и рисуем круг
        gc.setFill(Color.RED);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }
}
