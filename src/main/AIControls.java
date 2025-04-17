package main;

import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class AIControls {
    private final CheckBox workAIBox = new CheckBox("Интеллект рабочих");
    private final CheckBox warAIBox = new CheckBox("Интеллект военных");

    private final TextField warR = new TextField();
    private final TextField warV = new TextField();
    private final TextField workV = new TextField();

    private final Label warRText = new Label("R:");
    private final Label warVText = new Label("V:");
    private final Label workVText = new Label("V:");

    private final ComboBox<Integer> warComboBox = new ComboBox<>(FXCollections.observableArrayList(1,2,3,4,5,6,7,8,9,10));
    private final ComboBox<Integer> workComboBox = new ComboBox<>(FXCollections.observableArrayList(1,2,3,4,5,6,7,8,9,10));

    private static AIControls instance;

    private AIControls() {
        workAIBox.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");
        workAIBox.relocate(40, 225);

        warAIBox.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");
        warAIBox.relocate(40, 250);

        warR.relocate(60, 360);
        warR.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");
        warR.setText("50");

        warRText.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");
        warRText.setTextFill(Color.RED);
        warRText.relocate(40, 365);

        warV.relocate(60, 400);
        warV.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");
        warV.setText("4");

        warVText.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");
        warVText.setTextFill(Color.RED);
        warVText.relocate(40, 405);

        workV.relocate(60, 440);
        workV.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");
        workV.setText("1");

        workVText.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");
        workVText.setTextFill(Color.BLUE);
        workVText.relocate(40, 445);

        warComboBox.relocate(40, 500);
        warComboBox.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");
        warComboBox.setValue(5);

        workComboBox.relocate(40, 560);
        workComboBox.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");
        workComboBox.setValue(5);
    }

    public CheckBox getWorkAIBox() {
        return workAIBox;
    }

    public CheckBox getWarAIBox() {
        return warAIBox;
    }

    public TextField getWarR() {
        return warR;
    }

    public TextField getWarV() {
        return warV;
    }

    public TextField getWorkV() {
        return workV;
    }

    public Label getWarRText() {
        return warRText;
    }

    public Label getWarVText() {
        return warVText;
    }

    public Label getWorkVText() {
        return workVText;
    }

    public ComboBox<Integer> getWarComboBox() {
        return warComboBox;
    }

    public ComboBox<Integer> getWorkComboBox() {
        return workComboBox;
    }

    public static AIControls getInstance() {
        AIControls localInstance = instance;
        if (localInstance == null) {
            synchronized (AIControls.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new AIControls();
                }
            }
        }
        return localInstance;
    }
}
