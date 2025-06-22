package ru.nsu.t4werok.towerdefence.model.game.playerState.tech;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Узел дерева технологий. */
public class TechNode implements Serializable {
    private static final long serialVersionUID = 1L;

    /* -------- конфигурация -------- */
    private final String name;
    private final String description;
    private final int    cost;

    /* -------- состояние -------- */
    private boolean unlocked = false;

    /* -------- связи -------- */
    private TechNode parent;                     // новый «простой» родитель
    private final List<TechNode> prerequisites = new ArrayList<>();
    private final List<TechNode> children      = new ArrayList<>();

    public TechNode(String name, String descr, int cost) {
        this.name = name; this.description = descr; this.cost = cost;
    }

    /* -------- API, нужное контроллерам/ UI -------- */
    public String getName()        { return name; }
    public String getDescription() { return description; }
    public int    getCost()        { return cost; }

    public boolean isUnlocked()             { return unlocked; }
    public void    unlock()                 { unlocked = true; }
    public void    setUnlocked(boolean val) { unlocked = val; }

    public List<TechNode> getPrerequisites(){ return Collections.unmodifiableList(prerequisites); }
    public void addPrerequisite(TechNode n) { prerequisites.add(n); }

    public List<TechNode> getChildren()     { return Collections.unmodifiableList(children); }
    public void addChild(TechNode n)        {
        n.parent = this;                    // фиксируем родителя
        children.add(n);
    }
    public TechNode getParent()             { return parent; }

    /* -------- deep copy -------- */
    TechNode deepCopy() {
        TechNode c = new TechNode(name, description, cost);
        c.unlocked = unlocked;
        for (TechNode p : prerequisites) c.prerequisites.add(p.deepCopy());
        for (TechNode ch : children) {
            TechNode chCopy = ch.deepCopy();
            chCopy.parent = c;
            c.children.add(chCopy);
        }
        return c;
    }
}
