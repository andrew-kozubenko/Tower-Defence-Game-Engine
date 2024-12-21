package ru.nsu.t4werok.towerdefence.model.game.entities.map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Base {
    private int x; // Координата X базы
    private int y; // Координата Y базы
    private int health; // Здоровье базы

    @JsonCreator
    public Base(
            @JsonProperty("x") int x,
            @JsonProperty("y") int y,
            @JsonProperty("health") int health
    ) {
        this.x = x;
        this.y = y;
        this.health = health;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
    }

    public boolean isDestroyed() {
        return health <= 0;
    }
}