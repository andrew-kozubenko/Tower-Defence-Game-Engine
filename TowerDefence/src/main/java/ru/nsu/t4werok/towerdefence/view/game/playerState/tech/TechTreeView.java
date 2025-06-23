package ru.nsu.t4werok.towerdefence.view.game.playerState.tech;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Window;
import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerConfig;
import ru.nsu.t4werok.towerdefence.controller.game.GameController;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechNode;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechTree;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UI-слой для работы с технологиями.
 * • «Unlock upgrades» — иерархия анлока в один клик.<br>
 * • «Apply to …»    — применяем апгрейд и видим список уже установленных.
 */
public class TechTreeView {

    private final GameController gc;
    public  TechTreeView(GameController gc) { this.gc = gc; }

    /* ===================================================================== */
    /* ========================  ГЛОБАЛЬНЫЙ UNLOCK  ======================== */
    public void showUpgradesWindow(TowerConfig cfg) {
        Popup pop = new Popup();
        VBox  box = layout("Unlock upgrades – " + cfg.getName());
        box.setStyle("-fx-padding: 1; -fx-background-color: #F5F5F5; -fx-border-color: #333333; -fx-border-width: 1;");


        TechTree tree = cfg.getTechTree();
        if (tree == null) box.getChildren().add(new Label("No tech tree"));
        else for (TechNode root : tree.getRoots())
            addNodeUnlock(root, box, 0);


        pop.getContent().add(box);
        pop.setAutoHide(true);
        pop.show(primary());
    }

    /* ===================================================================== */
    /* =======================  APPLY К КОНКРЕТНОЙ БАШНЕ  ================== */
    public void showTowerUpgradeMenu(Tower tower, List<TechTree> trees) {
        Popup pop = new Popup();
        VBox  box = layout("Apply to " + tower.getName());

        /* ——— инфо по башне + уже применённые апгрейды ——— */
        box.getChildren().add(towerInfo(tower));
        box.getChildren().add(upgradesInfo(tower));

        /* ——— доступные для применения ——— */
        TechTree tree = trees.stream()
                .filter(t -> t.getTowerName().equals(tower.getName()))
                .findFirst().orElse(null);

        if (tree == null) box.getChildren().add(new Label("No tech tree"));
        else {
            List<TechNode> avail = tree.getAvailableUpgrades(tower);
            if (avail.isEmpty()) box.getChildren().add(new Label("Nothing to apply"));
            else for (TechNode n : avail) {
                Button b = new Button(n.getName() + " (" + n.getCost() + " coins)");
                b.setDisable(gc.coinsNow() < n.getCost());
                b.setOnAction(e -> {
                    if (gc.buyUpgradeForTower(tower, n)) {
                        pop.hide();                               // перерисовать окно
                        showTowerUpgradeMenu(tower, trees);
                    } else warn("Cannot apply", "Not enough coins.");
                });
                box.getChildren().add(b);
            }
        }
        pop.getContent().add(box);
        pop.setAutoHide(true);
        pop.show(primary());
    }

    /* ===================================================================== */
    /* ============================  HELPERS  ============================== */
    private VBox layout(String header) {
        VBox v = new VBox(10);
        v.setStyle("-fx-padding:10; -fx-background-color:#F5F5F5; -fx-border-color:#333; -fx-border-width:1;");
        v.getChildren().add(new Label(header));
        return v;
    }
    private Window primary() {
        return Window.getWindows().stream()
                .filter(Window::isShowing)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active window"));
    }
    private Label towerInfo(Tower t) {
        return new Label(
                "Level: "   + t.getUpgradeLevel() + "\n" +
                        "Damage: "  + t.getDamage()       + "\n" +
                        "Radius: "  + t.getAttackRadius() + "\n" +
                        "Fire rate: "+ t.getFireRate()
        );
    }
    /** Список уже применённых апгрейдов (виден сразу). */
    private Label upgradesInfo(Tower t) {
        String list = t.getUpgrades().isEmpty()
                ? "—"
                : t.getUpgrades().stream().collect(Collectors.joining(", "));
        return new Label("Applied upgrades: " + list);
    }

    /* ===================================================================== */
    /* ======================  РЕКУРСИВНЫЙ ВЫВОД UNLOCK  ==================== */
    private void addNodeUnlock(TechNode n, VBox parent, int depth) {
        HBox line = new HBox(8);
        line.setPadding(new Insets(0, 0, 0, depth * 20));
        line.setStyle("-fx-padding:5; -fx-background-color:#E8E8E8; -fx-border-color:#CCC;");

        Label info = new Label(n.getName() + " (" + n.getCost() + " coins)");
        info.setAlignment(Pos.CENTER_LEFT); // Центр по вертикали, если Label имеет высоту
        info.setPrefHeight(25); // Задаём высоту строки (как у кнопки)
        HBox.setMargin(info, new Insets(4, 0, 4, 0)); // Вертикальный отступ

        Button buy  = new Button(n.isUnlocked() ? "Unlocked" : "Unlock");

        buy.setDisable(n.isUnlocked() ||
                !gc.isUpgradeAvailable(n) ||
                gc.coinsNow() < n.getCost());

        buy.setOnAction(e -> {
            if (!gc.buyUpgrade(n)) {
                warn("Cannot unlock", "Not enough coins or prerequisites.");
                return;
            }
            buy.setText("Unlocked");
            buy.setDisable(true);
            for (TechNode child : n.getChildren())
                addNodeUnlock(child, parent, depth + 1);

//            updateHUD();
        });

        line.getChildren().addAll(info, buy);
        parent.getChildren().add(line);

        if (n.isUnlocked())
            for (TechNode child : n.getChildren())
                addNodeUnlock(child, parent, depth + 1);
    }

    private void warn(String h, String m) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Upgrade");
        a.setHeaderText(h);
        a.setContentText(m);
        a.showAndWait();
    }



}
