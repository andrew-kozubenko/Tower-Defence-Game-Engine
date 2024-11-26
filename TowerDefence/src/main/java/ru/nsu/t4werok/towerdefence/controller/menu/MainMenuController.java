package ru.nsu.t4werok.towerdefence.controller.menu;

import ru.nsu.t4werok.towerdefence.controller.SceneController;

public class MainMenuController {
    private final SceneController sceneController;

    public MainMenuController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    public void onPlayButtonPressed() {
        sceneController.switchTo("MapSelection");
    }

    public void onReplayButtonPressed() {
        sceneController.switchTo("ReplaySelection");
    }

    public void onSettingsButtonPressed() {
        sceneController.switchTo("Settings");
    }

    public void onExitButtonPressed() {
        System.exit(0);
    }
}

