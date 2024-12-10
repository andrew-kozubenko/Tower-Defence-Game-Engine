package ru.nsu.t4werok.towerdefence.controller.menu;

import javafx.stage.FileChooser;
import ru.nsu.t4werok.towerdefence.controller.SceneController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class MapSelectionController {
    private final SceneController sceneController;

    // Конструктор с передачей сцены
    public MapSelectionController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    // Метод для обработки нажатия кнопки "Back to Main Menu"
    public void onBackButtonPressed() {
        sceneController.switchTo("MainMenu");
    }

    // Метод для обработки выбора карты
    public void onMapSelected(String mapName) {
        if (mapName != null) {
            // Здесь должна быть логика запуска игры с выбранной картой
            System.out.println("Start game on map: " + mapName);
        } else {
            System.out.println("No map selected");
        }
    }

    // Метод для загрузки карт из конфигурационных файлов
    public List<String> loadMaps() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Map Configuration File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json") // Фильтруем только JSON-файлы
        );
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Documents/Games/Tower Defence/Maps"));

        // Показываем диалог выбора файла
        File selectedFile = fileChooser.showOpenDialog(null);

        List<String> maps = new ArrayList<>();
//        if (selectedFile != null) {
//            // Добавляем название выбранного файла в список карт
//            maps.add(selectedFile.getName());
//            System.out.println("Map file loaded: " + selectedFile.getAbsolutePath());
//        } else {
//            System.out.println("No map file selected.");
//        }
        return maps;
    }
}

