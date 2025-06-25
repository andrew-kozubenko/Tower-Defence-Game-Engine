package ru.nsu.t4werok.towerdefence.controller.game;

import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.app.GameEngine;
import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerSelectionConfig;
import ru.nsu.t4werok.towerdefence.config.game.playerState.tech.TechTreeConfig;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.controller.game.entities.tower.TowerController;
import ru.nsu.t4werok.towerdefence.controller.game.playerState.tech.TechTreeController;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.model.game.playerState.PlayerState;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechNode;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechTree;
import ru.nsu.t4werok.towerdefence.net.LocalMultiplayerContext;
import ru.nsu.t4werok.towerdefence.net.NetworkSession;
import ru.nsu.t4werok.towerdefence.view.game.entities.tower.TowerView;
import ru.nsu.t4werok.towerdefence.view.game.playerState.tech.TechTreeView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GameController {
    /* -------- прежние поля -------- */
    private final TowerController    towerController;
    private final TechTreeController techTreeController;
    private final GameMap            gameMap;
    private final List<Tower>        towers;
    private final SceneController    sceneController;
    private final GameEngine         gameEngine;

    private List<TowerConfig> towersForSelect;
    private List<TechTreeConfig> techTreeConfigs;
    private final List<TechTree> techTrees = new ArrayList<>();
    private TowerConfig selectedTower;

    private final PlayerState playerState = new PlayerState("Player", 10_000);

    /* --- сетевой слой --- */
    private NetworkSession networkSession;               // теперь изменяемый

    /* ---------------- ctor ---------------- */
    public GameController(GameEngine engine,
                          SceneController sc,
                          GameMap map,
                          List<Tower> sharedTowers) {
        this.gameEngine      = engine;
        this.sceneController = sc;
        this.gameMap         = map;
        this.towers          = sharedTowers;

        this.techTreeController = new TechTreeController(techTreeConfigs, playerState);
        this.towerController    = new TowerController(map, sharedTowers);

        /* берём сессию, если уже зарегистрирована */
        this.networkSession = LocalMultiplayerContext.get().getSession();
    }

    public List<TowerConfig> getTowersForSelect() {
        return towersForSelect;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setSelectedTower(TowerConfig selectedTower) {
        this.selectedTower = selectedTower;
    }

    public List<TechTree> getTechTrees() {
        return techTrees;
    }

    public void setTowersForSelect(List<TowerConfig> towersForSelect) {
        this.towersForSelect = towersForSelect;
    }

    /** Вызывается локальным UI. */
    /* ===================================================================
                          ПОСТАНОВКА БАШНИ (локально)
       =================================================================== */
    public Tower placeTower(int x, int y) {
        if (selectedTower == null) return null;
        if (playerState.getCoins() < selectedTower.getPrice()) return null;

        Tower t = towerController.addTower(selectedTower, x, y);
        if (t == null) return null;

        playerState.spendCoins(selectedTower.getPrice());

        if (isMultiplayer())
            networkSession.sendPlaceTower(selectedTower.getName(), x, y);

        return t;
    }

    /* ===================================================================
                       ПОСТАНОВКА БАШНИ (пришла по сети)
       =================================================================== */
    public synchronized void placeTowerRemote(String towerName, int x, int y) {
        if (towersForSelect == null) return;
        towersForSelect.stream()
                .filter(cfg -> cfg.getName().equals(towerName))
                .findFirst()
                .ifPresent(cfg -> towerController.addTower(cfg, x, y));
    }

    /* ===================================================================
                             утилиты
       =================================================================== */
    public void setNetworkSession(NetworkSession s) {    // ⭐ новый сеттер
        this.networkSession = s;
    }
    private boolean isMultiplayer() {
        return networkSession != null && networkSession.isConnected();
    }

    public boolean sellTower(Tower tower) {
        // Удаляем башню из списка
        if (!towers.remove(tower)) return false;

        // Сумма:
        double refund = tower.getPrice() * 0.7; // 70% возврат базовой цены

        // Округляем до сотых
        refund = Math.round(refund * 100.0) / 100.0;

        // Добавляем монеты игроку
        playerState.addCoins((int) refund);

        // отправка сообщения если мультиплеер
        if (isMultiplayer()) {
            networkSession.sendSellTower(tower.getX(), tower.getY());
        }

        // Обновляем интерфейс
//        updateHUD();
        return true;
    }


    public synchronized void sellTowerRemote(int x, int y) {

        if (towers == null) return;

        // Найти башню по координатам
        Optional<Tower> maybeTower = towers.stream()
                .filter(tower -> tower.getX() == x && tower.getY() == y)
                .findFirst();

        // Удалить после стрима
        maybeTower.ifPresent(tower -> {
            towers.remove(tower);
            towerController.removeTower(tower);  // удаляем
        });
    }

    public boolean buyUpgradeForTower(Tower tower, TechNode node) {
        // отправка сообщения если мультиплеер
        if (techTreeController.buyUpgradeForTower(tower, node)) {
            System.out.println("Upgrade");
            if (isMultiplayer()) {
                System.out.println("send upgrade");
                networkSession.sendUpgradeTower(tower.getX(), tower.getY());
            }
            return true;
        }
        return false;

    }

    public synchronized void upgradeTowerRemote(int x, int y) {
        System.out.println("1");
        if (towers == null) return;

        // Найти башню по координатам
        Optional<Tower> maybeTower = towers.stream()
                .filter(tower -> tower.getX() == x && tower.getY() == y)
                .findFirst();

        maybeTower.ifPresent(tower -> {
            TechTree techTree = getTechTrees().stream()
                    .filter(t -> t.getTowerName().equals(tower.getName()))
                    .findFirst().orElse(null);
            if (techTree == null) return;
            System.out.println("2");
            List<TechNode> allNodes = techTree.getAvailableUpgradesSend(tower);
            System.out.println("3");
            if (allNodes == null) return;
            System.out.println("4");

            List<String> applied = tower.getUpgrades();
            System.out.println("5");

            // Найти следующий ещё не применённый апгрейд
            for (TechNode node : allNodes) {

                if (applied.contains(node.getName())) continue;               // Уже применён
                System.out.println(node.getName());
//                if (!node.isUnlocked()) continue;                             // Глобально не открыт
                System.out.println(node.getName());
                techTreeController.applyUpgrade(tower, node.getName());          // Применяем
                tower.addUpgrade(node.getName());                            // Отмечаем как применённый
                System.out.println("upgrade all");
//                TowerView.showTowerUpgradeMenu(tower, getTechTrees());
                break;                                                       // Только один апгрейд за раз
            }
        });
    }



    public void selectTower(TowerConfig towerConfig) {
        this.selectedTower = towerConfig;
        System.out.println("Selected tower " + towerConfig.getName());
    }

    public void openSettings(Stage menuStage) {
        sceneController.switchTo("Settings");
        menuStage.close();
    }


    public void updateTower(List<Enemy> enemies) {
        towerController.updateTowers(enemies, playerState);
    }

    public void backToGame(Stage menuStage) {
        menuStage.close();
    }

    public void backToMenu(Stage menuStage) {
        gameEngine.setRunning(false);
        sceneController.switchTo("MainMenu");
        menuStage.close();
    }

    public void loadTowersForSelect() {
        // Загружаем все деревья технологий
        loadTechTrees();

        TowerSelectionConfig towerConfigLoader = new TowerSelectionConfig();
        this.towersForSelect = towerConfigLoader.loadTowers();

        // Для каждой башни находим соответствующее дерево технологий и устанавливаем его
        for (TowerConfig towerConfig : this.towersForSelect) {
            String towerName = towerConfig.getName();  // Имя башни совпадает с именем дерева технологий
            TechTree techTree = techTreeController.findTechTreeByName(towerName);  // Ищем дерево технологий по имени
            if (techTree != null) {
                towerConfig.setTechTree(techTree);  // Устанавливаем найденное дерево технологий в башню
                this.techTrees.add(techTree);
            } else {
                System.out.println("No tech tree found for tower: " + towerName);
            }
        }
    }

    public void loadTechTrees() {
        techTreeController.loadTechTrees();
    }

    public void stop() {
    }

    public boolean buyUpgrade(TechNode node) {
        return techTreeController.buyUpgrade(node);
    }

    public boolean isUpgradeAvailable(TechNode node) {
        return techTreeController.isUpgradeAvailable(node);
    }

    /* -------- методы работы с башнями -------- */
    private Optional<TowerConfig> findTowerConfigByName(String name) {
        return towersForSelect.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    public TowerConfig getSelectedTower() { return selectedTower; }

    public boolean checkTowerInCell(int x, int y) {
        for (Tower tower : towers) if (tower.getX() == x && tower.getY() == y) return true;
        return false;
    }

    public Tower getTowerAtCell(int x, int y) {
        for (Tower tower : towers) if (tower.getX() == x && tower.getY() == y) return tower;
        return null;
    }

    public int coinsNow() { return playerState.getCoins(); }


//    public void upgradeTowerRemote(int x, int y) {
//        // Найди башню по координатам и вызови логику улучшения
//        gameMap.upgradeTowerAt(x, y);
//    }

}
