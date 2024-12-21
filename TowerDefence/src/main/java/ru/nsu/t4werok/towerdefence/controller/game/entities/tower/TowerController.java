package ru.nsu.t4werok.towerdefence.controller.game.entities.tower;

import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerConfig;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;

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

    public List<Tower> loadTowerFromConfigs(String directoryPath) {
        // Тут тип вызываем loadFromJson из TowerConfig в цикле и возвращаем Список башен
        return new ArrayList<>();
    }

    public List<TowerConfig> getTowerConfigs() {
        return towerConfigs;
    }

    public boolean addTower(Tower tower) {
        // Проверяем, находится ли позиция внутри границ карты
        int x = tower.getX();
        int y = tower.getY();

        if (!gameMap.isWithinBounds(x, y)) {
            System.out.println("Position out of bounds: (" + x + ", " + y + ")");
            return false;
        }

        // Проверяем, доступна ли позиция для установки башни
        boolean positionAvailable = gameMap.getTowerPositions().stream()
                .anyMatch(pos -> pos[0] == x && pos[1] == y);

        if (!positionAvailable) {
            System.out.println("Tower cannot be placed at position: (" + x + ", " + y + ")");
            return false;
        }

        // Проверяем, нет ли уже башни в этой позиции
        boolean towerExists = towers.stream()
                .anyMatch(towerObj -> towerObj.getX() == x && towerObj.getY() == y);

        if (towerExists) {
            System.out.println("Tower already exists at position: (" + x + ", " + y + ")");
            return false;
        }

        // Добавляем башню
        Tower newTower = new Tower(x, y);
        towers.add(newTower);
        System.out.println("Tower added at position: (" + x + ", " + y + ")");
        return true;
    }
}
