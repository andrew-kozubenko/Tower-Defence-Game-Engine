package ru.nsu.t4werok.towerdefence.replay;

import ru.nsu.t4werok.towerdefence.app.GameEngine;
import ru.nsu.t4werok.towerdefence.replay.model.ReplayDataModel;
import ru.nsu.t4werok.towerdefence.replay.util.ReplayIO;

public class ReplayManager {
    private ReplayRecorder recorder;
    private ReplayPlayer player;
    private final GameEngine engine;

    public ReplayManager(GameEngine engine) {
        this.engine = engine;
    }

    public void startRecording() {
        recorder = new ReplayRecorder();
    }

    public void stopAndSave(String filePath) throws Exception {
        ReplayDataModel model = recorder.finish(engine.getGameMap().getMapName());
        ReplayIO.save(model, filePath);
    }

    public void loadAndPlay(String filePath) throws Exception {
        ReplayDataModel model = ReplayIO.load(filePath);
        player = new ReplayPlayer(model, engine);
        player.start();
    }

    public boolean isRecording() {
        return recorder != null;
    }

    public long getRecordingStartTime() {
        return recorder.getStartTime();
    }
}
