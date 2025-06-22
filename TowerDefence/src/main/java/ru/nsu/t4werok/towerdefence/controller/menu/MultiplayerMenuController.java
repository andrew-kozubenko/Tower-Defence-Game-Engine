package ru.nsu.t4werok.towerdefence.controller.menu;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.utils.ResourceManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * Multiplayer-меню: выбор карты + Host / Join.
 * <br/>Предоставляет {@link #getMaps()} —
 * его использует {@code MultiplayerMenuView}.
 */
public class MultiplayerMenuController {

    private final SceneController sceneController;

    /* ---------- список карт для ListView ---------- */
    private final ObservableList<String> maps = FXCollections.observableArrayList();

    /* ---------- выбранная карта (абсолютный путь) ---------- */
    private Path selectedMap;

    public MultiplayerMenuController(SceneController sceneController) {
        this.sceneController = sceneController;
        loadMaps();
    }

    /* ================== accessor для View ================== */

    /** Observable-список имён файлов карт (*.json). */
    public ObservableList<String> getMaps() { return maps; }

    /* ===================== выбор карты ===================== */

    /** Вызывается View при щелчке по элементу ListView. */
    public void selectMap(String fileName) {
        selectedMap = (fileName == null) ? null
                : ResourceManager.getMapsDir().resolve(fileName);
    }

    /** Вызывается SceneController, если нужно подсветить «предвыбранную» карту. */
    public void setSelectedMap(String absolutePath) {
        if (absolutePath == null) return;
        Path p = Path.of(absolutePath);
        if (Files.isRegularFile(p)) {
            selectedMap = p;
            String name = p.getFileName().toString();
            Platform.runLater(() -> {
                if (!maps.contains(name)) maps.add(name);
            });
        }
    }

    /* ====================== кнопки UI ====================== */

    public void onHostClicked(int port, String nickname) {
        if (selectedMap == null) return;                // карту не выбрали
        sceneController.openLobbyAsHost(port, nickname, selectedMap.toString());
    }

    public void onJoinClicked(String ip, int port, String nickname) {
        sceneController.openLobbyAsClient(ip.trim(), port, nickname);
    }

    public void onBackClicked() { sceneController.switchTo("MainMenu"); }

    /* ===================== helpers ========================= */

    private void loadMaps() {
        try {
            Files.createDirectories(ResourceManager.getMapsDir());

            var jsonFiles = Files.list(ResourceManager.getMapsDir())
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".json"))
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .collect(Collectors.toList());

            Platform.runLater(() -> {
                maps.setAll(jsonFiles);
                if (!jsonFiles.isEmpty()) selectMap(jsonFiles.get(0));
            });
        } catch (IOException ignored) { }
    }
}
