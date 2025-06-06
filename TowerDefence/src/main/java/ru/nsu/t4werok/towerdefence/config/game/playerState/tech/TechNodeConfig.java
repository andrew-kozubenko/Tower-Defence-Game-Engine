package ru.nsu.t4werok.towerdefence.config.game.playerState.tech;

import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechNode;

import java.util.List;

public class TechNodeConfig {
    private String name; // Название технологии
    private String description; // Описание технологии
    private Integer cost; // Стоимость изучения технологии
    private List<TechNodeConfig> prerequisites; // Необходимые технологии для разблокировки
    private List<TechNodeConfig> children; // Дочерние узлы (связанные технологии)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public List<TechNodeConfig> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<TechNodeConfig> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public List<TechNodeConfig> getChildren() {
        return children;
    }

    public void setChildren(List<TechNodeConfig> children) {
        this.children = children;
    }
}

