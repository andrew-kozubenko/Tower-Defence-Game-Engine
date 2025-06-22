package ru.nsu.t4werok.towerdefence.controller.game.playerState.tech;

import ru.nsu.t4werok.towerdefence.config.game.playerState.tech.*;
import ru.nsu.t4werok.towerdefence.managers.game.entities.tower.UpgradeManager;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.model.game.playerState.PlayerState;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechNode;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechTree;

import java.util.*;

/** Контроллер дерева технологий без циклов в prerequisites. */
public class TechTreeController {

    /* ---------- data ---------- */
    private final PlayerState player;
    private final Map<String, TechTree> templates = new HashMap<>();
    private List<TechTreeConfig> raw = new ArrayList<>();

    public TechTreeController(List<TechTreeConfig> cfgs, PlayerState player) {
        this.player = player;
        if (cfgs != null) { raw = cfgs; buildTemplates(); }
    }

    public void loadTechTrees() {
        raw = new TechTreeSelectionConfig().loadTechTrees();
        buildTemplates();
    }

    private void buildTemplates() {
        templates.clear();
        for (TechTreeConfig cfg : raw) templates.put(cfg.getName(), toTemplate(cfg));
    }

    /* ---------- public ---------- */
    public TechTree findTechTreeByName(String towerName) {
        TechTree tpl = templates.get(towerName);
        return tpl == null ? null : player.techTreeFor(towerName, tpl);
    }

    public boolean isUpgradeAvailable(TechNode n) { return canUnlock(n); }

    public boolean buyUpgrade(TechNode n) {
        if (!canUnlock(n) || !player.spendCoins(n.getCost())) return false;
        n.unlock(); return true;
    }

    public boolean buyUpgradeForTower(Tower tower, TechNode n) {
        if (!n.isUnlocked()) return false;                          // глобально не открыт
        if (tower.getUpgrades().contains(n.getName())) return false;
        if (!allParentsApplied(tower, n)) return false;
        if (!player.spendCoins(n.getCost())) return false;

        UpgradeManager.applyUpgrade(tower, n.getName());
        tower.addUpgrade(n.getName());
        return true;
    }

    /* ---------- availability helpers ---------- */
    private boolean canUnlock(TechNode n) {
        if (n.isUnlocked()) return false;
        if (n.getParent()!=null && !n.getParent().isUnlocked()) return false;
        for (TechNode pre : n.getPrerequisites()) if (!pre.isUnlocked()) return false;
        return true;
    }
    private boolean allParentsApplied(Tower t, TechNode n) {
        TechNode p = n.getParent();
        return p==null || t.getUpgrades().contains(p.getName());
    }

    /* ---------- template builder ---------- */
    private TechTree toTemplate(TechTreeConfig cfg) {
        TechTree tree = new TechTree(cfg.getName());
        for (TechNodeConfig r : cfg.getRoots()) tree.addRoot(build(r));
        return tree;
    }
    private TechNode build(TechNodeConfig c) {
        TechNode n = new TechNode(c.getName(), c.getDescription(), c.getCost());
        for (TechNodeConfig pre : c.getPrerequisites()) n.addPrerequisite(build(pre));
        for (TechNodeConfig ch  : c.getChildren())     n.addChild(build(ch));
        return n;
    }
}
