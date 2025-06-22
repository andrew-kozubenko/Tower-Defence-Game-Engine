package ru.nsu.t4werok.towerdefence.controller.menu;

import ru.nsu.t4werok.towerdefence.controller.SceneController;

import java.nio.file.Path;

/**
 * Расширен: теперь поддерживает кнопку «Multiplayer».
 */
public class MapSelectionController {

    private final SceneController sceneController;
    private Path selectedMap;               // текущий выбор

    public MapSelectionController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    /* ---------- события UI ---------- */

    public void onMapChosen(Path map) {
        selectedMap = map;
    }

    public void onPlaySingle() {
        if (selectedMap == null) return;
        // прежняя логика одиночной игры …
    }

    public void onPlayMultiplayer() {
        if (selectedMap == null) return;
        sceneController.showMultiplayerMenu(selectedMap.toString());
    }
}
