package ru.nsu.t4werok.towerdefence.model.game.entities.enemy;

import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;

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
    private int x;
    private int y; // Координаты врага

    private int numberOfPath;

    // Конструктор
    public Enemy(int lifePoints, int speed, int damageToBase, int loot, int startX, int startY, int numberOfPath) {
        this.lifePoints = lifePoints;
        this.speed = speed;
        this.damageToBase = damageToBase;
        this.loot = loot;
        this.numberOfPath = numberOfPath;
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

    public int getCurrentPathIndex() {
        return currentPathIndex;
    }

    public void setCurrentPathIndex(int currentPathIndex) {
        this.currentPathIndex = currentPathIndex;
    }

    // Логика движения по пути
    public void move(GameMap map) {
        // Вычисляем размеры клетки по осям X и Y
        int cellSizeX = 800 / map.getWidth();  // Размер клетки по X
        int cellSizeY = 600 / map.getHeight(); // Размер клетки по Y

        // Получаем путь для врага
        List<Integer[]> path = map.getEnemyPaths().get(numberOfPath); // Путь в клетках
        int someSpeed = speed;
        while (currentPathIndex < path.size() && someSpeed > 0) {

            Integer[] nextCell = path.get(currentPathIndex);

            // Вычисляем целевые координаты (центр следующей клетки)
            int targetX = nextCell[0] * cellSizeX + cellSizeX / 2;
            int targetY = nextCell[1] * cellSizeY + cellSizeY / 2;

            // Разница между текущими и целевыми координатами
            int dx = targetX - x;
            int dy = targetY - y;

            // Проверяем расстояние до текущей цели
            int distanceX = Math.abs(dx);
            int distanceY = Math.abs(dy);

            // Если враг может достичь цели за текущий такт
            if (distanceX <= speed && distanceY <= speed) {
                // Перемещаемся точно в цель
                x = targetX;
                y = targetY;

                // Уменьшаем скорость на пройденное расстояние
                someSpeed -= Math.max(distanceX, distanceY);

                // Переходим к следующей точке
                currentPathIndex++;
            } else {
                // Движемся в сторону цели ровно на скорость
                x += Integer.signum(dx) * Math.min(speed, distanceX);
                y += Integer.signum(dy) * Math.min(speed, distanceY);

                // Обнуляем скорость, так как текущий шаг завершен
                someSpeed = 0;
            }
        }

        // Если путь пройден до конца, враг достигает базы
        if (currentPathIndex >= path.size()) {
            setDead(true);
        }
    }




    public void takeDamage(int damage) {
        this.lifePoints -= damage;
        if (this.lifePoints <= 0) {
            setDead(true);
        }
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
}
