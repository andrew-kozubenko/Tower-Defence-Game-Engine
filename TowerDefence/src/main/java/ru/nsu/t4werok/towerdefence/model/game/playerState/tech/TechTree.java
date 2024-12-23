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

    // Рекурсивно добавляем зависимости для всех дочерних узлов
    public void fillPrerequisites() {
        for (TechNode root : roots) {
            addPrerequisitesRecursively(root, null); // Для корневых узлов нет родителей
        }
    }

    // Рекурсивный метод для добавления prerequisites
    private void addPrerequisitesRecursively(TechNode node, TechNode parent) {
        if (parent != null) {
            node.addPrerequisite(parent); // Добавляем родителя как prerequisite
        }

        // Проходим по всем дочерним узлам
        for (TechNode child : node.getChildren()) {
            addPrerequisitesRecursively(child, node); // Для каждого дочернего узла родитель становится prerequisite
        }
    }
}
