package main;

import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.File;

public class LoadSave {
    private final Button saveBtn = new Button("Сохранить");
    private final Button loadBtn = new Button("Загрузить");
    private final FileChooser fileChooser = new FileChooser();

    private static volatile LoadSave instance;

    private LoadSave() {
        loadBtn.relocate(40, 620);
        loadBtn.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");

        saveBtn.relocate(165, 620);
        saveBtn.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("TXT", "*.txt"));
        fileChooser.setInitialDirectory(new File("ants"));
    }

    public Button getSaveBtn() {
        return saveBtn;
    }

    public Button getLoadBtn() {
        return loadBtn;
    }

    public FileChooser getFileChooser() {
        return fileChooser;
    }

    public static LoadSave getInstance() {
        LoadSave localInstance = instance;
        if (localInstance == null) {
            synchronized (LoadSave.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new LoadSave();
                }
            }
        }
        return localInstance;
    }
}
