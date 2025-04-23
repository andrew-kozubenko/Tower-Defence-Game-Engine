package ru.nsu.t4werok.towerdefence.controller.menu;

import javafx.stage.FileChooser;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.utils.ResourceManager;

import java.io.File;

public class ReplaySelectionController {
    private final SceneController sceneController;

    public ReplaySelectionController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    public File chooseReplayFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose replay");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        // теперь из ResourceManager
        File replayDir = ResourceManager.getReplaysDir().toFile();
        replayDir.mkdirs();
        fileChooser.setInitialDirectory(replayDir);
        return fileChooser.showOpenDialog(null);
    }

    public void onBackButtonPressed() {
        sceneController.switchTo("MainMenu");
    }

    public void onPlayReplayButtonPressed(File replayFile) {
        if (replayFile != null) {
            // Здесь должна быть логика запуска реплея, например:
            System.out.println("Start replay: " + replayFile.getAbsolutePath());
        } else {
            System.out.println("Replay not choosen");
        }
    }
}
