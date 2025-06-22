package ru.nsu.t4werok.towerdefence.controller.menu;

import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.view.menu.MultiplayerMenuView;

public class MainMenuController {
    private final SceneController sceneController;

    public MainMenuController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    public void onPlayButtonPressed() {
        sceneController.switchTo("MapSelection");
    }

    public void onMultiplayerPressed() {
        MultiplayerMenuView view = new MultiplayerMenuView(new MultiplayerMenuController(sceneController));
        sceneController.addScene("MultiplayerMenu", view.getScene());
        sceneController.switchTo("MultiplayerMenu");
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

