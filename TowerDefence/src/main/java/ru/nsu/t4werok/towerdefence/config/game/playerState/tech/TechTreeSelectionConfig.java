package ru.nsu.t4werok.towerdefence.config.game.playerState.tech;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TechTreeSelectionConfig {

    private final String directoryPath = "techTree"; // Папка, где хранятся файлы конфигураций деревьев технологий

    /**
     * Метод для загрузки всех деревьев технологий из конфигурационных файлов
     *
     * @return Список объектов TechTreeConfig, представляющих параметры деревьев технологий
     */
    public List<TechTreeConfig> loadTechTrees() {
        File directory = new File(directoryPath);
        List<TechTreeConfig> techTreeConfigs = new ArrayList<>();

        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Tech trees directory not found: " + directoryPath);
            return techTreeConfigs;
        }

        // Получаем все файлы с расширением .json в указанной папке
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            ObjectMapper mapper = new ObjectMapper();
            for (File file : files) {
                try {
                    // Читаем JSON-файл и преобразуем его в объект TechTreeConfig
                    TechTreeConfig techTreeConfig = mapper.readValue(file, TechTreeConfig.class);
                    techTreeConfigs.add(techTreeConfig);
                    System.out.println("Loaded tech tree: " + file.getName());
                } catch (IOException e) {
                    System.err.println("Failed to load tech tree: " + file.getName());
                    e.printStackTrace();
                }
            }
        }

        return techTreeConfigs;
    }
}
