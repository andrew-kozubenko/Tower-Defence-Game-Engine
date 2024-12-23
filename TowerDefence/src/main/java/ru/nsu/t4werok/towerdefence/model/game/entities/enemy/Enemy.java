package ru.nsu.t4werok.towerdefence.model.game.entities.enemy;

import java.util.List;

public class Enemy {
    private int lifePoints;
    private int speed; // Скорость в кадрах
    private int defense;
    private int damageToBase; // Урон по базе
    private int loot;
    private List<String> animations;
    private boolean isDead;
    private int currentPathIndex; // Индекс текущей точки на пути
    private int x, y; // Координаты врага

    // Конструктор
    public Enemy(int lifePoints, int speed, int defense, int damageToBase, int loot, List<String> animations, int startX, int startY) {
        this.lifePoints = lifePoints;
        this.speed = speed;
        this.defense = defense;
        this.damageToBase = damageToBase;
        this.loot = loot;
        this.animations = animations;
        this.isDead = false;
        this.currentPathIndex = 0;
        this.x = startX;
        this.y = startY;
    }

    // Геттеры и сеттеры
    public int getLifePoints() { return lifePoints; }
    public void setLifePoints(int lifePoints) { this.lifePoints = lifePoints; }
    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }
    public int getDamageToBase() { return damageToBase; }
    public boolean isDead() { return isDead; }
    public void setDead(boolean isDead) { this.isDead = isDead; }
    public int getLoot() { return loot; }


    // Логика движения по пути
    public void move(List<Integer[]> path) {
        if (currentPathIndex < path.size()) {
            Integer[] nextPoint = path.get(currentPathIndex);
            this.x = nextPoint[0];
            this.y = nextPoint[1];
            currentPathIndex++;
        } else {
            // Достигнута база
            setDead(true); // Враг считается мертвым, когда достиг базы
        }
    }

    public void takeDamage(int damage) {
        this.lifePoints -= damage;
        if (this.lifePoints <= 0) {
            setDead(true);
        }
    }
}
