package ru.nsu.t4werok.towerdefence.view.game.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllEnemiesView {
    private final Map<Enemy, EnemyView> enemyViews = new HashMap<>();

    public void renderEnemies(GraphicsContext gc, List<Enemy> enemies) {
        // Проходим по списку врагов
        for (Enemy enemy : enemies) {
            // Если EnemyView для врага ещё не создан, создаём его
            enemyViews.computeIfAbsent(enemy, EnemyView::new);

            // Отрисовываем врага
            enemyViews.get(enemy).render(gc);
        }

        // Удаляем из мапы представления для врагов, которые больше не активны
        enemyViews.keySet().removeIf(enemy -> !enemies.contains(enemy));
    }
}
