package ru.nsu.t4werok.towerdefence.model.game.playerState.tech;

import java.util.ArrayList;
import java.util.List;

public class TechNode {
    private final String name; // Название технологии
    private final String description; // Описание технологии
    private final Integer cost; // Стоимость изучения технологии
    private final List<TechNode> prerequisites; // Необходимые технологии для разблокировки
    private final List<TechNode> children; // Дочерние узлы (связанные технологии)

    public TechNode(String name, String description, int cost) {
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.prerequisites = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    // Добавить зависимость (предыдущую технологию)
    public void addPrerequisite(TechNode prerequisite) {
        this.prerequisites.add(prerequisite);
    }

    // Добавить дочернюю технологию
    public void addChild(TechNode child) {
        this.children.add(child);
        child.addPrerequisite(this);
    }

    // Получить имя технологии
    public String getName() {
        return name;
    }

    // Получить описание технологии
    public String getDescription() {
        return description;
    }

    // Получить стоимость технологии
    public Integer getCost() {
        return cost;
    }

    // Получить список зависимостей
    public List<TechNode> getPrerequisites() {
        return prerequisites;
    }

    // Получить дочерние технологии
    public List<TechNode> getChildren() {
        return children;
    }
}