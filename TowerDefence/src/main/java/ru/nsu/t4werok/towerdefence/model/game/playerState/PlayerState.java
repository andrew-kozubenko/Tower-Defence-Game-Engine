package ru.nsu.t4werok.towerdefence.model.game.playerState;

import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechTree;

import java.io.Serializable;
import java.util.*;

/**
 * POJO-объект, описывающий одно «личное» состояние.
 *  • не содержит ссылок на JavaFX ‒ значит пригоден для headless-логики;
 *  • реализует Serializable ‒ чтобы при желании можно было писать сохранения
 *    или передавать снапшоты по сети (позже).
 */
public class PlayerState implements PlayerContext, Serializable {

    private static final long serialVersionUID = 1L;

    /* ------------------- идентификация ------------------- */

    private final UUID id;              // создаётся 1 раз и остаётся неизменным
    private String     name;

    /* ------------------- прогресс ------------------- */

    private int level      = 1;
    private int experience = 0;

    /* ------------------- ресурсы ------------------- */

    private int coins;
    private int health;

    /* ------------------- апгрейды ------------------- */

    /**
     * Каждый тип башни ↦ личное дерево технологий игрока.
     * Ключ ‒ тех-ID башни (обычно совпадает с именем конфиг-файла),
     * значение ‒ текущее «живое» дерево, где уже отмечены купленные ноды.
     */
    private final Map<String, TechTree> techTrees = new HashMap<>();

    /* ------------------- конструкторы ------------------- */

    public PlayerState(String name, int startCoins, int startHealth) {
        this(UUID.randomUUID(), name, startCoins, startHealth);
    }

    /** служебный конструктор (например, для восстановления из save-файла). */
    public PlayerState(UUID id, String name, int startCoins, int startHealth) {
        if (startCoins < 0 || startHealth < 0) {
            throw new IllegalArgumentException("Negative initial values are not allowed");
        }
        this.id     = id;
        this.name   = name;
        this.coins  = startCoins;
        this.health = startHealth;
    }

    /* ------------------- монеты ------------------- */

    @Override
    public boolean spendCoins(int amount) {
        requirePositive(amount);
        if (coins < amount) return false;
        coins -= amount;
        return true;
    }

    @Override
    public void addCoins(int amount) {
        requirePositive(amount);
        coins += amount;
    }

    @Override
    public int getCoins() { return coins; }

    /* ------------------- здоровье ------------------- */

    @Override
    public void damage(int dmg) {
        requirePositive(dmg);
        health = Math.max(0, health - dmg);
    }

    @Override
    public void heal(int value) {
        requirePositive(value);
        health += value;
    }

    @Override
    public int getHealth() { return health; }

    /* ------------------- Tech-деревья ------------------- */

    /**
     * Возвращает персональное дерево для указанного типа башни,
     * создавая «чистую» копию из базовой конфигурации при первом запросе.
     */
    public TechTree techTreeFor(String towerType, TechTree baseTreeTemplate) {
        return techTrees.computeIfAbsent(towerType, k -> baseTreeTemplate.copy());
    }

    /**
     * Быстрый просмотр, сколько нод уже куплено по данному типу башни.
     */
    public int purchasedUpgrades(String towerType) {
        TechTree tree = techTrees.get(towerType);
        return tree == null ? 0 : tree.purchasedCount();
    }

    /* ------------------- getters / setters ------------------- */

    @Override public UUID   getId()       { return id; }
    @Override public String getName()     { return name; }
    public  int             getLevel()    { return level; }
    public  int             getExperience(){ return experience; }

    public void addExperience(int xp) {
        requirePositive(xp);
        experience += xp;
        checkLevelUp();
    }

    private void checkLevelUp() {
        // простая шкала: каждые N*level очков – новый уровень
        int threshold = level * 100;
        while (experience >= threshold) {
            experience -= threshold;
            level++;
            threshold = level * 100;
        }
    }

    /* ------------------- util ------------------- */

    private static void requirePositive(int v) {
        if (v <= 0) throw new IllegalArgumentException("value must be positive");
    }

    @Override
    public String toString() {
        return "PlayerState{" +
                "id=" + id + ", name='" + name + '\'' +
                ", level=" + level + ", xp=" + experience +
                ", coins=" + coins + ", health=" + health +
                '}';
    }

    public PlayerState(String name, int startCoins) {
        this(name, startCoins, 100);   // 100 HP по умолчанию
    }

    public void setCoins(int value) {
        if (value < 0) throw new IllegalArgumentException("Coins may not be negative");
        this.coins = value;
    }
}
