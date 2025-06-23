package ru.nsu.t4werok.towerdefence.model.game.entities.tower;

import javafx.scene.image.Image;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Tower {
    private String name;
    private int x;
    private int y;
    private long lastAttackTime;
    private double attackRadius;
    private int price;
    private int damage;
    private String damageType;
    private double fireRate;
    private double reloadCounter;
    private int upgradeLevel;
    private String visualEffect;
    private Image imageTower;         // Картинка башни
    private int attackX;
    private int attackY;

    private List<String> upgrades = new ArrayList<>();
    private Enemy currentTarget;

    /**
     * Конструктор башни
     *
     * @param imageTowerPath относительный путь к изображению (например, "towers/tower1.png"),
     *                       который будет взят из "<user_home>/Documents/Games/TowerDefenceSD/...".
     */
    public Tower(String name,
                 int price,
                 int damage,
                 String damageType,
                 double fireRate,
                 String visualEffect,
                 int x,
                 int y,
                 double attackRadius,
                 String imageTowerPath) {
        this.name = name;
        this.price = price;
        this.damage = damage;
        this.damageType = damageType;
        this.fireRate = fireRate;
        this.upgradeLevel = 1;
        this.visualEffect = visualEffect;
        this.x = x;
        this.y = y;
        this.attackX = -1;
        this.attackY = -1;
        this.attackRadius = attackRadius;
        this.lastAttackTime = 0;

        // Формируем путь в Documents/Games/TowerDefenceSD
        Path docBasePath = Paths.get(
                System.getProperty("user.home"),
                "Documents",
                "Games",
                "TowerDefenceSD"
        );
        // Дополняем его относительным путём из imageTowerPath
        Path fullTowerImagePath = docBasePath.resolve(imageTowerPath);

        // Пытаемся загрузить
        try {
            File file = fullTowerImagePath.toFile();
            if (file.exists()) {
                this.imageTower = new Image(file.toURI().toString());
            } else {
                System.err.println("Tower image file not found: " + fullTowerImagePath);
                this.imageTower = null;
            }
        } catch (Exception e) {
            System.err.println("Error loading tower image: " + e.getMessage());
            this.imageTower = null;
        }
    }

    // --- Пример методов ---

    public double distanceTo(int enemyX, int enemyY) {
        return Math.sqrt(Math.pow(this.x - enemyX, 2) + Math.pow(this.y - enemyY, 2));
    }

    public String getName() {
        return name;
    }
    public int getPrice() {
        return price;
    }
    public int getDamage() {
        return damage;
    }
    public String getDamageType() {
        return damageType;
    }
    public double getFireRate() {
        return fireRate;
    }
    public int getUpgradeLevel() {
        return upgradeLevel;
    }
    public String getVisualEffect() {
        return visualEffect;
    }

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
    public void setPrice(int x) {
        this.price = price + x;
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
    public List<String> getUpgrades() {
        return upgrades;
    }
    public void addUpgrade(String upgrade) {
        this.upgrades.add(upgrade);
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
    public int getAttackX() {
        return attackX;
    }
    public void setAttackX(int attackX) {
        this.attackX = attackX;
    }
    public int getAttackY() {
        return attackY;
    }
    public void setAttackY(int attackY) {
        this.attackY = attackY;
    }
}
