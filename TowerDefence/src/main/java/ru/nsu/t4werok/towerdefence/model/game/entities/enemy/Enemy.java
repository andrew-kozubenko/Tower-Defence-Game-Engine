package ru.nsu.t4werok.towerdefence.model.game.entities.enemy;

import java.util.List;
import java.util.Map;

public class Enemy {
    private int lifePoints;
    private int speed;
    private int defense;
    private Map<String, Integer> strengths; // Тип урона и эффективность
    private Map<String, Integer> weaknesses; // Тип урона и слабость
    private int loot;
    private String model;
    private List<String> animations;
    private boolean isDead = false;

    // Конструктор
    public Enemy(int lifePoints, int speed, int defense, Map<String, Integer> strengths,
                 Map<String, Integer> weaknesses, int loot, String model, List<String> animations) {
        this.lifePoints = lifePoints;
        this.speed = speed;
        this.defense = defense;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.loot = loot;
        this.model = model;
        this.animations = animations;
    }

    // Геттеры и сеттеры
    public int getLifePoints() {
        return lifePoints;
    }

    public void setLifePoints(int lifePoints) {
        this.lifePoints = lifePoints;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public Map<String, Integer> getStrengths() {
        return strengths;
    }

    public void setStrengths(Map<String, Integer> strengths) {
        this.strengths = strengths;
    }

    public Map<String, Integer> getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(Map<String, Integer> weaknesses) {
        this.weaknesses = weaknesses;
    }

    public int getLoot() {
        return loot;
    }

    public void setLoot(int loot) {
        this.loot = loot;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<String> getAnimations() {
        return animations;
    }

    public void setAnimations(List<String> animations) {
        this.animations = animations;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    // Методы для обработки повреждений
    public void takeDamage(String damageType, int damage) {
        int effectiveDamage = damage;
        if (strengths.containsKey(damageType)) {
            effectiveDamage -= strengths.get(damageType); // Уменьшение урона
        } else if (weaknesses.containsKey(damageType)) {
            effectiveDamage += weaknesses.get(damageType); // Увеличение урона
        }
        effectiveDamage -= defense; // Учёт защиты
        if (effectiveDamage > 0) {
            lifePoints -= effectiveDamage;
        }

        if (lifePoints <= 0) {
            onDeath();
        }
    }

    private void onDeath() {
        System.out.println("Enemy defeated! Dropped loot: " + loot);
        // Write some logic
    }
}
