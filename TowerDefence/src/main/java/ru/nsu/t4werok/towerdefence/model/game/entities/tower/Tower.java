package ru.nsu.t4werok.towerdefence.model.game.entities.tower;

import javafx.scene.image.Image;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;

import java.io.File;

public class Tower {
    // Название башни (для идентификации)
    private String name;

    private int x;
    private int y;

    // Время последней атаки (в миллисекундах)
    private long lastAttackTime;

    // Радиус действия башни (в игровых единицах)
    private double attackRadius;

    // Стоимость строительства или улучшения башни
    private int price;

    // Урон, который наносит башня
    private int damage;

    // Тип урона, например, физический, магический и т.д.
    private String damageType;

    // Скорость стрельбы (в атаке)
    private double fireRate;

    private double reloadCounter; // Счетчик оставшихся итераций до следующей атаки

    // Текущий уровень улучшения башни
    private int upgradeLevel;

    // Визуальный эффект башни (например, анимация или цвет)
    private String visualEffect;

    // Картинка башни
    private Image imageTower;

    private Enemy currentTarget; // Текущая цель для атаки


    /**
     * Конструктор для создания новой башни с указанными параметрами.
     * @param name Название башни.
     * @param price Стоимость строительства.
     * @param damage Урон, наносимый башней.
     * @param damageType Тип урона (например, "физический" или "магический").
     * @param fireRate Скорость стрельбы (выстрелы в секунду).
     * @param visualEffect Визуальный эффект (например, "огонь" или "магия").
     * @param x Координата X.
     * @param y Координата Y.
     * @param attackRadius Радиус атаки.
     */
    public Tower(String name, int price, int damage, String damageType, double fireRate, String visualEffect, int x,
                 int y, double attackRadius, String imageTowerPath) {
        this.name = name;
        this.price = price;
        this.damage = damage;
        this.damageType = damageType;
        this.fireRate = fireRate;
        this.upgradeLevel = 1; // Установим начальный уровень улучшений
        this.visualEffect = visualEffect;
        this.x = x;
        this.y = y;
        this.attackRadius = attackRadius;
        this.lastAttackTime = 0;
        try {
            // Преобразуем путь в URL
            File file = new File(imageTowerPath);
            if (file.exists()) {
                this.imageTower = new Image(file.toURI().toString());
            } else {
                System.err.println("Background image file not found: " + imageTowerPath);
                this.imageTower = null; // Если файл отсутствует, устанавливаем null
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
            this.imageTower = null;
        }
    }


    // Метод вычисления расстояния до врага
    public double distanceTo(int enemyX, int enemyY) {
        return Math.sqrt(Math.pow(this.x - enemyX, 2) + Math.pow(this.y - enemyY, 2));
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

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setDamageType(String damageType) {
        this.damageType = damageType;
    }

    public void setFireRate(double fireRate) {
        this.fireRate = fireRate;
    }

    public void setUpgradeLevel(int upgradeLevel) {
        this.upgradeLevel = upgradeLevel;
    }

    public double getAttackRadius() {
        return attackRadius;
    }

    public void setAttackRadius(double attackRadius) {
        this.attackRadius = attackRadius;
    }

    public long getLastAttackTime() {
        return lastAttackTime;
    }

    public void setLastAttackTime(long lastAttackTime) {
        this.lastAttackTime = lastAttackTime;
    }

    public Image getImageTower() {
        return imageTower;
    }

    public void setImageTower(Image imageTower) {
        this.imageTower = imageTower;
    }

    public Enemy getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(Enemy currentTarget) {
        this.currentTarget = currentTarget;
    }
    public double getReloadCounter() {
        return reloadCounter;
    }

    public void setReloadCounter(double reloadCounter) {
        this.reloadCounter = reloadCounter;
    }

}
