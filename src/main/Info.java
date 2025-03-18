package main;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Info {

    private Text time = new Text();
    private Stage modalStage = new Stage();
    private Text infoText = new Text();
    private CheckBox toggleInfo = new CheckBox("Показывать статистику");

    private Button closeButton = new Button("OK");
    private Button continueButton = new Button("Отмена");

    public Info(int X, int Y, Stage stage) {

        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Статистика");
        modalStage.setResizable(false);

        time.relocate(102, 30);

        closeButton.relocate(80, 140);
        continueButton.relocate(150, 140);

        Pane layout = new Pane(time, infoText, closeButton, continueButton);
        modalStage.setScene(new Scene(layout, 300, 200));

        modalStage.initOwner(stage);

        infoText.relocate(122, 70);
        toggleInfo.relocate(X, Y);
        toggleInfo.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");
    }

    public void printInfo(List<AbstractAnt> ants, String timer) {

        int countWarriors = 0, countWorkers = 0;
        for (AbstractAnt ant : ants) {
            if (ant instanceof WarriorAnt)
                countWarriors++;
            else
                countWorkers++;
        }

        infoText.setText("Военных: " + countWarriors + "\nРабочих: " + countWorkers);
        infoText.setVisible(true);

        time.setText(timer);
    }

    public CheckBox getToggleInfo() {
        return toggleInfo;
    }

    public void showStats(ArrayList<AbstractAnt> ants, String timer) {
        printInfo(ants, timer);
        modalStage.showAndWait();
    }

    public Button getContinueButton() {
        return continueButton;
    }

    public Button getCloseButton() {
        return closeButton;
    }

    public void close() {
        modalStage.close();
    }
}