package ru.nsu.t4werok.towerdefence.model.game.entities.tower;

public class Tower {
    // Название башни (для идентификации)
    private String name;

    // Стоимость строительства или улучшения башни
    private int price;

    // Урон, который наносит башня
    private int damage;

    // Тип урона, например, физический, магический и т.д.
    private String damageType;

    // Скорость стрельбы (в выстрелах в секунду)
    private double fireRate;

    // Текущий уровень улучшения башни
    private int upgradeLevel;

    // Визуальный эффект башни (например, анимация или цвет)
    private String visualEffect;

    /**
     * Конструктор для создания новой башни с указанными параметрами.
     * @param name Название башни.
     * @param price Стоимость строительства.
     * @param damage Урон, наносимый башней.
     * @param damageType Тип урона (например, "физический" или "магический").
     * @param fireRate Скорость стрельбы (выстрелы в секунду).
     * @param visualEffect Визуальный эффект (например, "огонь" или "магия").
     */
    public Tower(String name, int price, int damage, String damageType, double fireRate, String visualEffect) {
        this.name = name;
        this.price = price;
        this.damage = damage;
        this.damageType = damageType;
        this.fireRate = fireRate;
        this.upgradeLevel = 1; // Установим начальный уровень улучшений
        this.visualEffect = visualEffect;
    }

    // Геттеры и сеттеры для доступа к полям

    /**
     * Получить название башни.
     * @return Название башни.
     */
    public String getName() {
        return name;
    }

    /**
     * Получить текущую стоимость башни.
     * @return Стоимость башни.
     */
    public int getPrice() {
        return price;
    }

    /**
     * Получить текущий урон башни.
     * @return Урон башни.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Получить тип урона башни.
     * @return Тип урона башни.
     */
    public String getDamageType() {
        return damageType;
    }

    /**
     * Получить текущую скорость стрельбы башни.
     * @return Скорость стрельбы башни.
     */
    public double getFireRate() {
        return fireRate;
    }

    /**
     * Получить текущий уровень улучшения башни.
     * @return Уровень улучшения башни.
     */
    public int getUpgradeLevel() {
        return upgradeLevel;
    }

    /**
     * Получить текущий визуальный эффект башни.
     * @return Визуальный эффект башни.
     */
    public String getVisualEffect() {
        return visualEffect;
    }

    /**
     * Установить новый визуальный эффект башни.
     * @param visualEffect Новый визуальный эффект.
     */
    public void setVisualEffect(String visualEffect) {
        this.visualEffect = visualEffect;
    }
}
