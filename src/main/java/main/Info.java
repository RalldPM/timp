package main;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class Info {

    private final Text time = new Text();
    private final Stage modalStage = new Stage();
    private final Text infoText = new Text();

    private final Stage infoAntsStage = new Stage();
    private final Pane infoAntsPane = new Pane();

    private final CheckBox toggleInfo = new CheckBox("Показывать статистику");
    private final Button realTimeInfoBtn = new Button("Информация");

    private final Button closeButton = new Button("OK");
    private final Button continueButton = new Button("Отмена");

    private static volatile Info instance;

    private Info(int X, int Y) {

        // окно с количеством муравьев и таймером
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Статистика");
        modalStage.setResizable(false);

        time.relocate(102, 30);

        closeButton.relocate(80, 140);
        continueButton.relocate(150, 140);

        Pane layout = new Pane(time, infoText, closeButton, continueButton);
        modalStage.setScene(new Scene(layout, 300, 200));

        infoText.relocate(122, 70);

        // окно про каждого из муавьёв
        infoAntsStage.initModality(Modality.APPLICATION_MODAL);
        infoAntsStage.setTitle("Информация о муравьях");
        infoAntsStage.setResizable(false);
        infoAntsStage.setScene(new Scene(infoAntsPane, 300, 700));

        // кнопки в панели управления
        toggleInfo.relocate(X, Y);
        toggleInfo.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");
        realTimeInfoBtn.relocate(X, Y + 100);
        realTimeInfoBtn.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");
        realTimeInfoBtn.setDisable(true);
    }

    public void setOwnerStage(Stage stage) {
        infoAntsStage.initOwner(stage);
        modalStage.initOwner(stage);
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

    public void showStats(Vector<AbstractAnt> ants, String timer) {
        printInfo(ants, timer);
        modalStage.showAndWait();
    }

    public void showAntsInfo(TreeMap<Integer, Integer> ants) {
        infoAntsPane.getChildren().clear();
        int posY = 30;
        for (Map.Entry<Integer, Integer> entry : ants.entrySet()) {
            String prof;
            int lifeTime;
            if (entry.getKey() / 10000 == 1) {
                prof = "Рабочий муравей";
                lifeTime = WorkerAnt.getLifeTime();
            }
            else {
                prof = "Военный муравей";
                lifeTime = WarriorAnt.getLifeTime();
            }
            Text infoAboutAnt = new Text(entry.getKey() + "\t" + prof + "\t" + entry.getValue() / 100 + "\t" + (entry.getValue() / 100 + lifeTime));
            infoAboutAnt.relocate(30, posY);
            infoAntsPane.getChildren().add(infoAboutAnt);
            posY += 20;
        }
        infoAntsStage.showAndWait();
    }

    public Button getContinueButton() {
        return continueButton;
    }

    public Button getCloseButton() {
        return closeButton;
    }

    public Button getInfoBtn() {
        return realTimeInfoBtn;
    }

    public Stage getAntsStage() {
        return infoAntsStage;
    }

    public void close() {
        modalStage.close();
    }

    public static Info getInstance() {
        Info localInstance = instance;
        if (localInstance == null) {
            synchronized (Info.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Info(40, 200);
                }
            }
        }
        return localInstance;
    }
}