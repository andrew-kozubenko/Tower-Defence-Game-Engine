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
        tower.setFireRate((long) (tower.getFireRate() * 0.9)); // Уменьшаем время перезарядки
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

    /**
     * Обновление состояния всех башен (атака врагов).
     * @param enemies Список врагов на карте.
     * @param currentTime Текущее игровое время.
     */
    public void updateTowers(List<Enemy> enemies, long currentTime) {
        for (Tower tower : towers) {
            // Находим ближайшего врага в радиусе башни
            Enemy target = findNearestEnemy(tower, enemies);
            if (target != null) {
                // Атакуем врага
                attackEnemy(tower, target, currentTime);
            }
        }
    }

    /**
     * Поиск ближайшего врага в радиусе башни.
     * @param tower Башня, которая ищет цель.
     * @param enemies Список врагов.
     * @return Ближайший враг или null, если врагов нет.
     */
    private Enemy findNearestEnemy(Tower tower, List<Enemy> enemies) {
//        return enemies.stream()
//                .filter(enemy -> isEnemyInRange(tower, enemy))
//                .min((e1, e2) -> Double.compare(
//                        distance(tower.getX(), tower.getY(), e1.getX(), e1.getY()),
//                        distance(tower.getX(), tower.getY(), e2.getX(), e2.getY())
//                ))
//                .orElse(null);
        return null;
    }

    /**
     * Проверка, находится ли враг в радиусе действия башни.
     * @param tower Башня.
     * @param enemy Враг.
     * @return true, если враг в радиусе действия; false в противном случае.
     */
//    private boolean isEnemyInRange(Tower tower, Enemy enemy) {
//        double distance = distance(tower.getX(), tower.getY(), enemy.getX(), enemy.getY());
//        return distance <= tower.getAttackRadius();
//    }


    /**
     * Вычисление расстояния между двумя точками.
     * @param x1 Координата X первой точки.
     * @param y1 Координата Y первой точки.
     * @param x2 Координата X второй точки.
     * @param y2 Координата Y второй точки.
     * @return Расстояние между точками.
     */
    private double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Атака врага башней.
     * @param tower Башня, которая атакует.
     * @param enemy Враг, который является целью.
     * @param currentTime Текущее игровое время.
     */
    private void attackEnemy(Tower tower, Enemy enemy, long currentTime) {
//        // Проверяем, готова ли башня стрелять
//        if (currentTime - tower.getLastAttackTime() < tower.getFireRate()) {
//            return; // Башня еще перезаряжается
//        }
//
//        // Наносим урон врагу
//        enemy.takeDamage(tower.getDamage());
//        tower.setLastAttackTime(currentTime); // Обновляем время последней атаки
//
//        // Проверяем, уничтожен ли враг
//        if (enemy.getHealth() <= 0) {
//            System.out.println("Enemy destroyed at position: (" + enemy.getX() + ", " + enemy.getY() + ")");
//        } else {
//            System.out.println("Enemy attacked at position: (" + enemy.getX() + ", " + enemy.getY() + "), Remaining health: " + enemy.getHealth());
//        }
    }


}
