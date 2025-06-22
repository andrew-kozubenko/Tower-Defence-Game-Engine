package ru.nsu.t4werok.towerdefence.model.game;

import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Отдельное хранилище башен и врагов.
 * UI-слой будет читать эти списки, а логика — изменять.
 * Списки thread-safe, чтобы в дальнейшем можно было
 * безопасно читать их в другом потоке (например, сетевом).
 */
public class GameWorld {

    private final List<Enemy> enemies = new CopyOnWriteArrayList<>();
    private final List<Tower> towers  = new CopyOnWriteArrayList<>();

    public List<Enemy> getEnemies() { return enemies; }
    public List<Tower> getTowers()  { return towers;  }
}
