package ru.nsu.t4werok.towerdefence.config.game.entities.enemy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.nsu.t4werok.towerdefence.config.game.entities.map.MapConfig;

import java.io.IOException;

public class EnemyConfig {

    private int lifePoints;
    private int speed;
    private int damageToBase;
    private int loot;

    @JsonProperty
    public int getLifePoints() {
        return lifePoints;
    }

    @JsonProperty
    public void setLifePoints(int lifePoints) {
        this.lifePoints = lifePoints;
    }

    @JsonProperty
    public int getSpeed() {
        return speed;
    }

    @JsonProperty
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @JsonProperty
    public int getDamageToBase() {
        return damageToBase;
    }

    @JsonProperty
    public void setDamageToBase(int damageToBase) {
        this.damageToBase = damageToBase;
    }

    @JsonProperty
    public int getLoot() {
        return loot;
    }

    @JsonProperty
    public void setLoot(int loot) {
        this.loot = loot;
    }
}
