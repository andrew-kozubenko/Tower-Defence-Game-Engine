package ru.nsu.t4werok.towerdefence.replay.model;

import ru.nsu.t4werok.towerdefence.replay.model.event.ReplayEvent;
import java.util.List;

public class ReplayDataModel {
    private String mapName;
    private long totalDuration;
    private List<ReplayEvent> events;

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public List<ReplayEvent> getEvents() {
        return events;
    }

    public void setEvents(List<ReplayEvent> events) {
        this.events = events;
    }
}
