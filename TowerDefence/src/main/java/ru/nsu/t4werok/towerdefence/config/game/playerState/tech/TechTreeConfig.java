package ru.nsu.t4werok.towerdefence.config.game.playerState.tech;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TechTreeConfig {
    private String name;
    private List<TechNodeConfig> roots;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TechNodeConfig> getRoots() {
        return roots;
    }

    public void setRoots(List<TechNodeConfig> roots) {
        this.roots = roots;
    }

    // Метод для загрузки данных из JSON в текущий объект
    public void loadFromJson(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TechTreeConfig loadedConfig = mapper.readValue(new File(filePath), TechTreeConfig.class);
        this.roots = loadedConfig.getRoots();
    }

    // Метод для сохранения текущего объекта в JSON
    public static void saveToJson(TechTreeConfig config, String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), config);
    }
}
