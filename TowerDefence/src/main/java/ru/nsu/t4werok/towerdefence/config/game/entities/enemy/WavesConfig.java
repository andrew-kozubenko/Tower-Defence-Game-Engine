package ru.nsu.t4werok.towerdefence.config.game.entities.enemy;

import ru.nsu.t4werok.towerdefence.config.game.entities.map.MapConfig;

import java.io.IOException;

public class WavesConfig {
    private WaveConfig[] waves;
    public WaveConfig[] getWaves() {
        return waves;
    }

    public void setWaves(WaveConfig[] waves) {
        this.waves = waves;
    }
}
