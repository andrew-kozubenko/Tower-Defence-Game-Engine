package ru.nsu.t4werok.towerdefence.replay;

import ru.nsu.t4werok.towerdefence.replay.model.ReplayDataModel;
import ru.nsu.t4werok.towerdefence.replay.model.event.ReplayEvent;
import java.util.ArrayList;
import java.util.List;

public class ReplayRecorder {
    private final long startTime;
    private final List<ReplayEvent> events = new ArrayList<>();

    public ReplayRecorder() {
        this.startTime = System.currentTimeMillis();
    }

    public void record(ReplayEvent e) {
        events.add(e);
    }

    public ReplayDataModel finish(String mapName) {
        ReplayDataModel model = new ReplayDataModel();
        model.setMapName(mapName);
        model.setTotalDuration(System.currentTimeMillis() - startTime);
        model.setEvents(events);
        return model;
    }

    public long getStartTime() {
        // TODO: сделать работающий getStartTime при записывании реплея
        return 0;
    }
}
