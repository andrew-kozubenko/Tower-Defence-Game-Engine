package ru.nsu.t4werok.towerdefence.view.game.playerState.tech;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
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
        Popup popup = new Popup();
        VBox upgradesLayout = new VBox(10);
        upgradesLayout.setStyle("-fx-padding: 10; -fx-background-color: #F5F5F5; -fx-border-color: #333333; -fx-border-width: 1;");
        upgradesLayout.setPrefWidth(400);
        upgradesLayout.setPrefHeight(600);

        upgradesLayout.getChildren().add(new Label("Upgrades for " + towerConfig.getName()));

        // Получаем дерево технологий
        TechTree techTree = towerConfig.getTechTree();
        if (techTree != null) {
            for (TechNode techNode : techTree.getRoots()) {
                addTechNodeToView(techNode, upgradesLayout, popup);
            }
        } else {
            upgradesLayout.getChildren().add(new Label("No upgrades available"));
        }

        popup.getContent().add(upgradesLayout);
        popup.setAutoHide(true); // Закрытие при клике вне окна
        popup.show(getPrimaryWindow());
    }

    private Window getPrimaryWindow() {
        return javafx.stage.Window.getWindows().stream()
                .filter(Window::isShowing)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active window found"));
    }

    public void showTowerUpgradeMenu(Tower tower, List<TechTree> techTrees) {
        Popup popup = new Popup();
        VBox menuLayout = new VBox(10);
        menuLayout.setStyle("-fx-padding: 10; -fx-background-color: #F0F0F0; -fx-border-color: #CCCCCC;");

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

        Label towerInfoLabel = new Label(
                "Tower Info:\n" +
                        "Level: " + tower.getUpgradeLevel() + "\n" +
                        "Damage: " + tower.getDamage() + "\n" +
                        "Attack Radius: " + tower.getAttackRadius() + "\n" +
                        "Fire Rate: " + tower.getFireRate()
        );

        menuLayout.getChildren().add(towerInfoLabel);

        List<TechNode> availableUpgrades = techTree.getAvailableUpgrades(tower);
        for (TechNode node : availableUpgrades) {
            Button upgradeButton = new Button(node.getName() + " (" + node.getCost() + " coins)");
            upgradeButton.setOnAction(e -> {
                boolean success = gameController.buyUpgradeForTower(tower, node);
                if (success) {
                    popup.hide();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Upgrade Failed");
                    alert.setHeaderText("Cannot purchase upgrade");
                    alert.setContentText("You don't have enough coins.");
                    alert.showAndWait();
                }
            });
            menuLayout.getChildren().add(upgradeButton);
        }

        popup.getContent().add(menuLayout);
        popup.setAutoHide(true);
        popup.show(getPrimaryWindow());
    }


    // Рекурсивно добавляет узлы дерева технологий в интерфейс
    private void addTechNodeToView(TechNode node, VBox parentLayout, Popup popup) {
        HBox nodeBox = new HBox(10);
        nodeBox.setStyle("-fx-padding: 5; -fx-background-color: #E8E8E8; -fx-border-color: #CCCCCC;");
        nodeBox.setPrefHeight(40);

        String description = String.format("%s (Cost: %d)", node.getName(), node.getCost());
        Button techButton = new Button(description);

        Button buyButton = new Button("Buy");
        buyButton.setOnAction(e -> {
            boolean success = gameController.buyUpgrade(node);
            if (success) {
                popup.hide(); // Закрыть при успешной покупке
            } else {
                // Показать предупреждение пользователю
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Upgrade Failed");
                alert.setHeaderText("Cannot purchase upgrade");
                alert.setContentText("You don't have enough coins.");
                alert.showAndWait();
            }
        });

        buyButton.setDisable(!gameController.isUpgradeAvailable(node));

        nodeBox.getChildren().addAll(techButton, buyButton);
        parentLayout.getChildren().add(nodeBox);

        for (TechNode child : node.getChildren()) {
            addTechNodeToView(child, parentLayout, popup);
        }
    }
}
