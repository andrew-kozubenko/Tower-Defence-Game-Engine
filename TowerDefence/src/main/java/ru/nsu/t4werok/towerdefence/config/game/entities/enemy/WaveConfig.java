package ru.nsu.t4werok.towerdefence.config.game.entities.enemy;

public class WaveConfig {
    private int[] enemies;
    private int interval;

    public int[] getEnemies() {
        return enemies;
    }

    public void setEnemies(int[] enemies) {
        this.enemies = enemies;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
