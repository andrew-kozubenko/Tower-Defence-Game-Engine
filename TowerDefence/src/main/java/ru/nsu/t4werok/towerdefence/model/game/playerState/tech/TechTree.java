package ru.nsu.t4werok.towerdefence.model.game.playerState.tech;

import java.util.ArrayList;
import java.util.List;

public class TechTree {
    private final List<TechNode> roots; // Корневые технологии (начальные)

    public TechTree() {
        this.roots = new ArrayList<>();
    }

    // Добавить корневую технологию
    public void addRoot(TechNode root) {
        this.roots.add(root);
    }

    // Получить все корневые технологии
    public List<TechNode> getRoots() {
        return roots;
    }
}
