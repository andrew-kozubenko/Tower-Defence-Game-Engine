package ru.nsu.t4werok.towerdefence.net;

import ru.nsu.t4werok.towerdefence.app.GameEngine;
import ru.nsu.t4werok.towerdefence.net.protocol.NetMessage;

/**
 * Singleton, через который сеть доставляет события в текущий GameEngine
 * и откуда клиентский код берёт активную NetworkSession.
 */
public final class LocalMultiplayerContext {

    private static final LocalMultiplayerContext I = new LocalMultiplayerContext();
    public static LocalMultiplayerContext get() { return I; }

    private volatile GameEngine     engine;
    private volatile NetworkSession session;

    private LocalMultiplayerContext() {}

    /* ---------- привязки ---------- */
    public void bindEngine(GameEngine e){ this.engine = e; }
    public void registerSession(NetworkSession s){ this.session = s; }
    public NetworkSession getSession(){ return session; }

    /* ---------- входящие NetMessage ---------- */
    public void dispatch(NetMessage msg){
        if (engine != null) engine.handleNetworkMessage(msg);
    }
}
