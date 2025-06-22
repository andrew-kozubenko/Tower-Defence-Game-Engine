package ru.nsu.t4werok.towerdefence.core.session;

import ru.nsu.t4werok.towerdefence.model.game.GameWorld;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.playerState.PlayerState;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Содержит ВСЁ состояние партии ― карту, мир, игроков.
 * Пока используется только локально, но его можно
 * одним методом сериализовать и отправить клиенту.
 */
public class GameSession {

    private final GameWorld world;
    private GameMap map;

    private final List<PlayerState> players = new CopyOnWriteArrayList<>();

    public GameSession(GameMap map, PlayerState owner) {
        this.world = new GameWorld();
        this.map   = map;
        this.players.add(owner);
    }

    /* -------- players -------- */

    public List<PlayerState> getPlayers() { return Collections.unmodifiableList(players); }
    public void addPlayer(PlayerState ps) { players.add(ps);       }
    public PlayerState getOwner()        { return players.get(0); }

    /* -------- world / map -------- */

    public GameWorld getWorld() { return world; }

    public GameMap getMap()           { return map; }
    public void    setMap(GameMap m ) { this.map = m; }
}
