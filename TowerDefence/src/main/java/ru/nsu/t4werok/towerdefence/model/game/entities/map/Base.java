package ru.nsu.t4werok.towerdefence.model.game.entities.map;

public class Base {
    private final int x; // Координата X базы
    private final int y; // Координата Y базы
    private int health; // Здоровье базы

    public Base(int x, int y, int health) {
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