package ru.nsu.t4werok.towerdefence.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.nsu.t4werok.towerdefence.config.game.entities.enemy.WavesConfig;

import java.io.File;
import java.io.IOException;

public class WavesLoader {
    public static WavesConfig loadWavesConfig(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(filePath), WavesConfig.class);
    }
}
