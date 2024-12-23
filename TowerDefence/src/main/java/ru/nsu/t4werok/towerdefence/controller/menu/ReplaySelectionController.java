package ru.nsu.t4werok.towerdefence.controller.menu;

import javafx.stage.FileChooser;
import ru.nsu.t4werok.towerdefence.controller.SceneController;

import java.io.File;

public class ReplaySelectionController {
    private final SceneController sceneController;

    public ReplaySelectionController(SceneController sceneController) {
        this.sceneController = sceneController;
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

    public File chooseReplayFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose replay");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );

        String replayDirPath = System.getProperty("user.home") + "/Documents/Games/Tower Defence/Replays";
        File replayDir = new File(replayDirPath);

        // Проверяем, существует ли папка, и создаём её, если она отсутствует
        if (!replayDir.exists()) {
            if (replayDir.mkdirs()) {
                System.out.println("Replay directory created: " + replayDirPath);
            } else {
                System.err.println("Failed to create replay directory: " + replayDirPath);
            }
        }

        fileChooser.setInitialDirectory(replayDir);
        return fileChooser.showOpenDialog(null);
    }

}
