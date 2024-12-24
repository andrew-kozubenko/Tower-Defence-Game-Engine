package ru.nsu.t4werok.towerdefence.controller.game.entities.tower;

import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerConfig;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.view.game.entities.tower.TowerView;

import java.util.ArrayList;
import java.util.List;

public class TowerController {
    private final GameMap gameMap;
    private final List<Tower> towers;

    public TowerController(GameMap gameMap, List<Tower> towers) {
        this.gameMap = gameMap;
        this.towers = towers;
    }

    private final List<TowerConfig> towerConfigs = new ArrayList<>();


    public Tower addTower(TowerConfig towerConfig, int x, int y) {

        // Проверяем, находится ли позиция внутри границ карты
        if (!gameMap.isWithinBounds(x, y)) {
            System.out.println("Position out of bounds: (" + x + ", " + y + ")");
            return null;
        }

        // Проверяем, доступна ли позиция для установки башни
        boolean positionAvailable = gameMap.getTowerPositions().stream()
                .anyMatch(pos -> pos[0] == x && pos[1] == y);

        if (!positionAvailable) {
            System.out.println("Tower cannot be placed at position: (" + x + ", " + y + ")");
            return null;
        }

        // Проверяем, нет ли уже башни в этой позиции
        boolean towerExists = towers.stream()
                .anyMatch(towerObj -> towerObj.getX() == x && towerObj.getY() == y);

        if (towerExists) {
            System.out.println("Tower already exists at position: (" + x + ", " + y + ")");
            return null;
        }

        // Добавляем башню
        Tower newTower = new Tower(
                towerConfig.getName(),
                towerConfig.getPrice(),
                towerConfig.getDamage(),
                towerConfig.getDamageType(),
                towerConfig.getFireRate(),
                towerConfig.getVisualEffect(),
                x, y,
                towerConfig.getAttackRadius(),
                towerConfig.getImage()
        );
        towers.add(newTower);
        System.out.println("Tower added at position: (" + x + ", " + y + ")");
        return newTower;
    }

    // удаление башни
    public boolean removeTower(Tower tower) {
        if (towers.remove(tower)) {
            System.out.println("Tower removed from position: (" + tower.getX() + ", " + tower.getY() + ")");
            return true;
        } else {
            System.out.println("Tower not found at position: (" + tower.getX() + ", " + tower.getY() + ")");
            return false;
        }
    }

    /**
     * Улучшение башни (повышение характеристик).
     * @param tower Башня, которую нужно улучшить.
     * @return true, если улучшение успешно; false, если улучшить нельзя.
     */
    public boolean upgradeTower(Tower tower) {
        // Проверяем, можно ли улучшить башню
        if (tower.getUpgradeLevel() >= 5) { // Максимальный уровень улучшений
            System.out.println("Tower is already at max upgrade level.");
            return false;
        }

        int upgradeCost = calculateUpgradeCost(tower);
        // Здесь должна быть проверка ресурсов игрока (например, `playerHasEnoughResources(upgradeCost)`)

        // Улучшаем параметры башни
        tower.setUpgradeLevel(tower.getUpgradeLevel() + 1);
        tower.setDamage(tower.getDamage() + 10);
        tower.setAttackRadius(tower.getAttackRadius() + 1.0);
        tower.setFireRate(tower.getFireRate() * 0.9); // Уменьшаем время перезарядки
        System.out.println("Tower upgraded to level " + tower.getUpgradeLevel());
        return true;
    }

    /**
     * Вычисление стоимости улучшения башни.
     * @param tower Башня, для которой вычисляется стоимость улучшения.
     * @return Стоимость улучшения.
     */
    private int calculateUpgradeCost(Tower tower) {
        return tower.getPrice() * (tower.getUpgradeLevel() + 1);
    }


    public void updateTowers(List<Enemy> enemies) {
        for (Tower tower : towers) {
            if (tower.getReloadCounter() > 0) {
                tower.setReloadCounter(tower.getReloadCounter() - 1);
            }

            // Если башня уже выбрала цель, продолжаем стрелять по ней, пока она в радиусе
            if (tower.getCurrentTarget() != null && isEnemyInRange(tower, tower.getCurrentTarget())) {
                attackEnemy(tower, tower.getCurrentTarget(), enemies);
            } else {
                // Ищем нового ближайшего врага, если текущий враг уничтожен или вышел из радиуса
                Enemy target = findNearestEnemy(tower, enemies);
                if (target != null) {
                    tower.setCurrentTarget(target); // Устанавливаем нового врага
                    attackEnemy(tower, target, enemies); // Атакуем сразу, если цель найдена
                }
            }
        }
    }


    // Проверка, находится ли враг в радиусе атаки башни
    private boolean isEnemyInRange(Tower tower, Enemy enemy) {
        double distance = Math.sqrt(Math.pow(enemy.getX() - tower.getX(), 2) + Math.pow(enemy.getY() - tower.getY(), 2));
        return distance <= tower.getAttackRadius();
    }



    // Поиск ближайшего врага для каждой башни
    private Enemy findNearestEnemy(Tower tower, List<Enemy> enemies) {
        Enemy nearestEnemy = null;
        double closestDistance = tower.getAttackRadius(); // Максимальное расстояние в радиусе атаки

        for (Enemy enemy : enemies) {
            double distance = Math.sqrt(Math.pow(enemy.getX() - tower.getX(), 2) + Math.pow(enemy.getY() - tower.getY(), 2));
            if (distance <= tower.getAttackRadius() && (nearestEnemy == null || distance < closestDistance)) {
                nearestEnemy = enemy;
                closestDistance = distance;
            }
        }

        return nearestEnemy; // Возвращаем ближайшего врага или null, если нет подходящего
    }


    // Атака врага
    private void attackEnemy(Tower tower, Enemy enemy, List<Enemy> enemies) {
        // Если башня ещё перезаряжается, пропускаем атаку
        if (tower.getReloadCounter() > 0) {
            return;
        }

        // Наносим урон врагу
        enemy.takeDamage(tower.getDamage());
        tower.setReloadCounter(tower.getFireRate()); // Сбрасываем перезарядку

        if (enemy.isDead()) {
            enemies.remove(enemy);
            System.out.println("Enemy destroyed at position: (" + enemy.getX() + ", " + enemy.getY() + ")");
            tower.setCurrentTarget(null); // Убираем цель, если враг уничтожен
        } else {
            System.out.println("Enemy attacked at position: (" + enemy.getX() + ", " + enemy.getY() + "), Remaining health: " + enemy.getLifePoints());
        }
    }




}
