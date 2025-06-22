package ru.nsu.t4werok.towerdefence.controller.menu;

import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.view.menu.MultiplayerMenuView;

/**
 * Главное меню.
 * «Multiplayer» → сразу окно Host/Join+выбор карты
 */
public class MainMenuController {

    private final SceneController sceneController;

    public MainMenuController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    public void onPlayButtonPressed()               { sceneController.switchTo("MapSelection"); }
    public void onReplayButtonPressed()             { sceneController.switchTo("ReplaySelection"); }
    public void onSettingsButtonPressed()           { sceneController.switchTo("Settings"); }
    public void onExitButtonPressed()               { System.exit(0); }

    public void onMultiplayerButtonPressed() {
        if (!sceneController.hasScene("MultiplayerMenu")) {
            MultiplayerMenuController c = new MultiplayerMenuController(sceneController);
            MultiplayerMenuView v      = new MultiplayerMenuView(c);
            sceneController.addScene("MultiplayerMenu", v.getScene());
        }
        sceneController.switchTo("MultiplayerMenu");
    }
}
