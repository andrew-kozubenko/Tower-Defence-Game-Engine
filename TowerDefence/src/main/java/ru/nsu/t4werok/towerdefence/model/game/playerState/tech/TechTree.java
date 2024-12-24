package ru.nsu.t4werok.towerdefence.model.game.playerState.tech;

import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;

import java.util.ArrayList;
import java.util.List;

public class TechTree {
    private final String name;
    private final List<TechNode> roots; // Корневые технологии (начальные)

    public TechTree(String name) {
        this.name = name;
        this.roots = new ArrayList<>();
    }

    public String getName() {
        return name;
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

    public List<TechNode> getAvailableUpgrades(Tower tower) {
        List<TechNode> availableUpgrades = new ArrayList<>();
        for (TechNode node : roots) {
            collectAvailableUpgrades(tower, node, availableUpgrades);
        }
        return availableUpgrades;
    }

    private void collectAvailableUpgrades(Tower tower, TechNode node, List<TechNode> availableUpgrades) {
        if (!tower.getUpgrades().contains(node.getName()) && node.isUnlocked()) {
            availableUpgrades.add(node);
        }
        for (TechNode child : node.getChildren()) {
            collectAvailableUpgrades(tower, child, availableUpgrades);
        }
    }
}
