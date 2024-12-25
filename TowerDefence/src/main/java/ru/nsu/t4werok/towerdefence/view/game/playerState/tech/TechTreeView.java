package ru.nsu.t4werok.towerdefence.view.game.playerState.tech;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerConfig;
import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechNode;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechTree;

import java.util.List;

public class TechTreeView {
    private final GameController gameController;

    public TechTreeView(GameController gameController) {
        this.gameController = gameController;
    }

    public void showUpgradesWindow(TowerConfig towerConfig) {
        Stage upgradesStage = new Stage();
        upgradesStage.setTitle("Upgrades for " + towerConfig.getName());

        VBox upgradesLayout = new VBox(10);
        upgradesLayout.setStyle("-fx-padding: 10; -fx-background-color: #F5F5F5;");

        // Получаем дерево технологий для башни
        TechTree techTree = towerConfig.getTechTree();
        if (techTree != null) {
            for (TechNode techNode : techTree.getRoots()) {
                addTechNodeToView(techNode, upgradesLayout, gameController);
            }
        } else {
            upgradesLayout.getChildren().add(new Button("No upgrades available"));
        }

        Scene upgradesScene = new Scene(upgradesLayout, 400, 600);
        upgradesStage.setScene(upgradesScene);
        upgradesStage.show();
    }

    public void showTowerUpgradeMenu(Tower tower, List<TechTree> techTrees) {
        Stage upgradeStage = new Stage();
        VBox menuLayout = new VBox(10);
        menuLayout.setStyle("-fx-padding: 5;");

        TechTree techTree = null;
        for (TechTree techTreeI : techTrees) {
            if (techTreeI.getName().equals(tower.getName())) {
                techTree = techTreeI;
                break;
            }
        }

        if (techTree == null) {
            System.out.println("No tech tree associated with this tower.");
            return;
        }

        // Информация о характеристиках башни
        Label towerInfoLabel = new Label();
        towerInfoLabel.setText("Tower Info:\n" +
                "Level: " + tower.getUpgradeLevel() + "\n" +
                "Damage: " + tower.getDamage() + "\n" +
                "Attack Radius: " + tower.getAttackRadius() + "\n" +
                "Fire Rate: " + tower.getFireRate() + "\n" +
                "Speed: " + tower.getFireRate());

        menuLayout.getChildren().add(towerInfoLabel); // Добавляем информацию о башне в меню

        // Получаем доступные улучшения
        List<TechNode> availableUpgrades = techTree.getAvailableUpgrades(tower);

        for (TechNode node : availableUpgrades) {
            Button upgradeButton = new Button(node.getName() + " (" + node.getCost() + " coins)");
            upgradeButton.setOnAction(e -> {
                gameController.buyUpgradeForTower(tower, node);
                upgradeStage.close();
            });
            menuLayout.getChildren().add(upgradeButton);
        }

        Scene upgradeScene = new Scene(menuLayout, 300, 200);
        upgradeStage.setScene(upgradeScene);
        upgradeStage.setTitle("Upgrades for " + tower.getName());
        upgradeStage.show();
    }

    // Рекурсивно добавляет узлы дерева технологий в интерфейс
    private void addTechNodeToView(TechNode node, VBox parentLayout, GameController gameController) {
        HBox nodeBox = new HBox(10);
        nodeBox.setStyle("-fx-padding: 5; -fx-background-color: #E8E8E8; -fx-border-color: #CCCCCC;");
        nodeBox.setPrefHeight(40);

        // Текст с описанием узла
        String description = String.format("%s (Cost: %d)", node.getName(), node.getCost());
        Button techButton = new Button(description);

        // Кнопка для покупки улучшения
        Button buyButton = new Button("Buy");
        buyButton.setOnAction(e -> gameController.buyUpgrade(node));

        // Проверяем доступность узла
        buyButton.setDisable(!gameController.isUpgradeAvailable(node));
//        buyButton.setDisable(!gameController.isBought(node));

        nodeBox.getChildren().addAll(techButton, buyButton);
        parentLayout.getChildren().add(nodeBox);

        // Рекурсивно добавляем дочерние узлы
        for (TechNode child : node.getChildren()) {
            addTechNodeToView(child, parentLayout, gameController);
        }
    }
}
