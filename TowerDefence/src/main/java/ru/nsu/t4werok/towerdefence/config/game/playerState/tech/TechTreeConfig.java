package ru.nsu.t4werok.towerdefence.config.game.playerState.tech;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TechTreeConfig {
    private List<TechNodeConfig> nodes;

    public List<TechNodeConfig> getNodes() {
        return nodes;
    }

    public void setNodes(List<TechNodeConfig> nodes) {
        this.nodes = nodes;
    }

    // Метод для загрузки данных из JSON в текущий объект
    public void loadFromJson(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TechTreeConfig loadedConfig = mapper.readValue(new File(filePath), TechTreeConfig.class);
        this.nodes = loadedConfig.getNodes();
    }

    // Метод для сохранения текущего объекта в JSON
    public static void saveToJson(TechTreeConfig config, String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), config);
    }
}
