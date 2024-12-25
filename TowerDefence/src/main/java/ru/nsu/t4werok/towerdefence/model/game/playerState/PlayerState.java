package ru.nsu.t4werok.towerdefence.model.game.playerState;

import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechTree;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechNode;

import java.util.ArrayList;
import java.util.List;

public class PlayerState {
    private String name; // Имя игрока
    private int level; // Уровень игрока
    private int experience; // Опыт игрока
    private int coins; // Валюта игрока
    private int health; // Здоровье игрока
    private int maxHealth; // Максимальное здоровье

    public PlayerState(String name, int initialHealth) {
        this.name = name;
        this.level = 1;
        this.experience = 0;
        this.coins = 0;
        this.health = initialHealth;
        this.maxHealth = initialHealth;
    }

    // Получить имя игрока
    public String getName() {
        return name;
    }

    // Установить имя игрока
    public void setName(String name) {
        this.name = name;
    }

    // Получить уровень игрока
    public int getLevel() {
        return level;
    }

    // Увеличить уровень игрока
    public void levelUp() {
        this.level++;
    }

    // Получить текущий опыт
    public int getExperience() {
        return experience;
    }

    // Добавить опыт игроку
    public void addExperience(int amount) {
        this.experience += amount;
    }

    // Получить количество монет
    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    // Добавить монеты игроку
    public void addCoins(int amount) {
        this.coins += amount;
    }

    // Уменьшить количество монет
    public boolean spendCoins(int amount) {
        if (amount > coins) {
            return false; // Недостаточно монет
        }
        this.coins -= amount;
        return true;
    }

    // Получить текущее здоровье
    public int getHealth() {
        return health;
    }

    // Установить здоровье игрока
    public void setHealth(int health) {
        if (health > maxHealth) {
            this.health = maxHealth; // Ограничение по максимуму
        } else if (health < 0) {
            this.health = 0; // Здоровье не может быть отрицательным
        } else {
            this.health = health;
        }
    }

    // Уменьшить здоровье
    public void takeDamage(int damage) {
        setHealth(this.health - damage);
    }

    // Восстановить здоровье
    public void heal(int amount) {
        setHealth(this.health + amount);
    }

    // Получить максимальное здоровье
    public int getMaxHealth() {
        return maxHealth;
    }

    // Увеличить максимальное здоровье (например, при улучшениях)
    public void increaseMaxHealth(int amount) {
        this.maxHealth += amount;
        this.health = Math.min(this.health, maxHealth); // Актуализировать текущее здоровье
    }


}
