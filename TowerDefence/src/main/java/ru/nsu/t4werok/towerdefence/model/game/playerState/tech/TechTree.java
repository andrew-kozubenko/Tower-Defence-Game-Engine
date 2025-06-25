package ru.nsu.t4werok.towerdefence.model.game.playerState.tech;

import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Дерево технологий для одного типа башни.
 * Содержит:
 *   • копирование (copy) без циклов,
 *   • рекурсивный подсчёт купленных узлов (purchasedCount),
 *   • выбор доступных апгрейдов для конкретной башни.
 */
public class TechTree implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String towerName;
    private final List<TechNode> roots = new ArrayList<>();

    public TechTree(String towerName) { this.towerName = towerName; }

    /* ---------- базовый API ---------- */
    public String getTowerName()    { return towerName; }
    public List<TechNode> getRoots(){ return Collections.unmodifiableList(roots); }
    public void addRoot(TechNode n) { roots.add(n); }
    public void fillPrerequisites() { /* compatibility no-op */ }

    /* ---------- рекурсивный подсчёт купленных узлов ---------- */
    public int purchasedCount() {
        return countUnlocked(roots);
    }
    private int countUnlocked(List<TechNode> nodes) {
        int total = 0;
        for (TechNode n : nodes) {
            if (n.isUnlocked()) total++;
            total += countUnlocked(n.getChildren());
        }
        return total;
    }

    /* ---------- глубокое копирование ---------- */
    public TechTree copy() {
        TechTree clone = new TechTree(towerName);
        for (TechNode r : roots) clone.roots.add(r.deepCopy());
        return clone;
    }

    /* ---------- список доступных для APPLY узлов ---------- */
    public List<TechNode> getAvailableUpgrades(Tower tower) {
        List<TechNode> out = new ArrayList<>();
        collectAvailable(roots, tower, out);
        return out;
    }
    private void collectAvailable(List<TechNode> src, Tower tower, List<TechNode> out) {
        for (TechNode n : src) {
            boolean parentsApplied =
                    n.getParent() == null || tower.getUpgrades().contains(n.getParent().getName());

            if (n.isUnlocked() && parentsApplied &&
                    !tower.getUpgrades().contains(n.getName())) {
                out.add(n);
            }
            collectAvailable(n.getChildren(), tower, out);
        }
    }

    public List<TechNode> getAvailableUpgradesSend(Tower tower) {
        List<TechNode> out = new ArrayList<>();
        collectAvailableSend(roots, tower, out);
        return out;
    }
    private void collectAvailableSend(List<TechNode> src, Tower tower, List<TechNode> out) {
        for (TechNode n : src) {
            out.add(n); // добавляем вообще все узлы (доступные, недоступные, применённые и неприменённые)
            collectAvailableSend(n.getChildren(), tower, out);
        }
    }
}
