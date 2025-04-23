package ru.nsu.t4werok.towerdefence.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Центральный менеджер путей к ресурсам TowerDefenceSD.
 */
public final class ResourceManager {
    // Корневая папка всех конфигов и ассетов
    private static final Path BASE_PATH =
            Paths.get(System.getProperty("user.home"), "Documents", "Games", "TowerDefenceSD");

    private ResourceManager() {}

    /** Путь к корню ресурсов */
    public static Path getBasePath() {
        return BASE_PATH;
    }

    /** Папка с картами (.json) */
    public static Path getMapsDir() {
        return BASE_PATH.resolve("maps");
    }

    /** Папка с конфигами башен */
    public static Path getTowersDir() {
        return BASE_PATH.resolve("towers");
    }

    /** Папка с деревьями технологий */
    public static Path getTechTreeDir() {
        return BASE_PATH.resolve("techTree");
    }

    /** Папка с настройками */
    public static Path getSettingsDir() {
        return BASE_PATH.resolve("settings");
    }

    /** Папка с реплеями */
    public static Path getReplaysDir() {
        return BASE_PATH.resolve("Replays");
    }

    /** Путь к enemy/enemies.json */
    public static Path getEnemiesConfigFile() {
        return BASE_PATH.resolve(Paths.get("enemy", "enemies.json"));
    }

    /** Путь к waves/waves.json */
    public static Path getWavesConfigFile() {
        return BASE_PATH.resolve(Paths.get("waves", "waves.json"));
    }
}
