package ru.nsu.t4werok.towerdefence.view.game.playerState.tech;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerConfig;
import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechNode;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechTree;

public class TechTreeView {

    public TechTreeView() {
    }

    public void showUpgradesWindow(GameController gameController, TowerConfig towerConfig) {
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

        nodeBox.getChildren().addAll(techButton, buyButton);
        parentLayout.getChildren().add(nodeBox);

        // Рекурсивно добавляем дочерние узлы
        for (TechNode child : node.getChildren()) {
            addTechNodeToView(child, parentLayout, gameController);
        }
    }
}
