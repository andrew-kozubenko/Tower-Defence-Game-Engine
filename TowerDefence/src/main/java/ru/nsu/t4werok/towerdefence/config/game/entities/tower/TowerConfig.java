package ru.nsu.t4werok.towerdefence.config.game.entities.tower;

public class TowerConfig {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getDamageType() {
        return damageType;
    }

    public void setDamageType(String damageType) {
        this.damageType = damageType;
    }

    public double getFireRate() {
        return fireRate;
    }

    public void setFireRate(double fireRate) {
        this.fireRate = fireRate;
    }

    public int getUpgradeLevel() {
        return upgradeLevel;
    }

    public void setUpgradeLevel(int upgradeLevel) {
        this.upgradeLevel = upgradeLevel;
    }

    public String getVisualEffect() {
        return visualEffect;
    }

    public void setVisualEffect(String visualEffect) {
        this.visualEffect = visualEffect;
    }
}
