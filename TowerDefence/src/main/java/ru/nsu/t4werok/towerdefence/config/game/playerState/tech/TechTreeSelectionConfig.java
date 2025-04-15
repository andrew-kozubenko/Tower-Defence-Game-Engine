package ru.nsu.t4werok.towerdefence.config.game.playerState.tech;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TechTreeSelectionConfig {

    // Путь к папке вида: <папка_пользователя>/Documents/Games/TowerDefenceSD/techTree
    private final Path techTreeDirectoryPath;

    public TechTreeSelectionConfig() {
        this.techTreeDirectoryPath = Paths.get(
                System.getProperty("user.home"),
                "Documents",
                "Games",
                "TowerDefenceSD",
                "techTree"
        );
        try {
            // Создаём директорию (и все недостающие) при отсутствии
            Files.createDirectories(techTreeDirectoryPath);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для дерева технологий: " + techTreeDirectoryPath, e);
        }
    }

    /**
     * Метод для загрузки всех деревьев технологий из конфигурационных файлов JSON
     *
     * @return Список объектов TechTreeConfig, представляющих параметры деревьев технологий
     */
    public List<TechTreeConfig> loadTechTrees() {
        List<TechTreeConfig> techTreeConfigs = new ArrayList<>();
        File directory = techTreeDirectoryPath.toFile();

        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Tech trees directory not found: " + techTreeDirectoryPath);
            return techTreeConfigs;
        }

        // Получаем все файлы с расширением .json в указанной папке
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            ObjectMapper mapper = new ObjectMapper();
            for (File file : files) {
                try {
                    // Читаем JSON-файл и преобразуем его в TechTreeConfig
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
