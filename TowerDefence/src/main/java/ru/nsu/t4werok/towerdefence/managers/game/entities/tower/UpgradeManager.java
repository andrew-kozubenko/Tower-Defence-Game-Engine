package ru.nsu.t4werok.towerdefence.managers.game.entities.tower;

import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;

public class UpgradeManager {
    public static void applyUpgrade(Tower tower, String upgrade) {
        if (!tower.getUpgrades().contains(upgrade)) {
            switch (upgrade) {
                case "ImproveDamage":
                    // Улучшаем урон башни, например, увеличиваем на 20%
                    tower.setDamage((int)  Math.round(tower.getDamage() * 1.2));
//                    tower.setPrice();
                    System.out.println("Tower damage increased!");
                    break;

                case "ImproveRange":
                    // Улучшаем радиус действия башни на 15%
                    tower.setAttackRadius(Math.round(tower.getAttackRadius() * 1.15 * 100.0) / 100.0);
                    System.out.println("Tower range increased!");
                    break;

                case "ImproveFireRate":
                    // Уменьшаем время между выстрелами (увеличиваем частоту стрельбы)
                    tower.setFireRate(Math.round(tower.getFireRate() * 0.8 * 100.0) / 100.0); // Уменьшаем fireRate на 20%
                    System.out.println("Tower fire rate increased!");
                    break;

                default:
                    System.out.println("Unknown upgrade: " + upgrade);
                    return;
            }
        }
    }
}
