package ru.nsu.t4werok.towerdefence.replay;

import javafx.animation.AnimationTimer;
import ru.nsu.t4werok.towerdefence.app.GameEngine;
import ru.nsu.t4werok.towerdefence.replay.model.ReplayDataModel;
import ru.nsu.t4werok.towerdefence.replay.model.event.ReplayEvent;

import java.util.Iterator;

public class ReplayPlayer {
    private final ReplayDataModel model;
    private final GameEngine engine;
    private long startTime;
    private Iterator<ReplayEvent> it;

    public ReplayPlayer(ReplayDataModel model, GameEngine engine) {
        this.model = model;
        this.engine = engine;
        this.it = model.getEvents().iterator();
    }

    public void start() {
        startTime = System.currentTimeMillis();
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsed = System.currentTimeMillis() - startTime;
                while (it.hasNext() && it.next().getTimestamp() <= elapsed) {
                    ReplayEvent e = it.next();
                    e.apply(engine);
                }
                if (!it.hasNext()) {
                    stop();
                }
            }
        }.start();
    }
}
