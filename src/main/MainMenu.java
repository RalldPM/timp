package main;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class MainMenu {

    private final TextField textFieldN1 = new TextField();
    private final TextField textFieldN2 = new TextField();
    private final TextField textFieldLifeTimeWork = new TextField();
    private final TextField textFieldLifeTimeWar = new TextField();
    private final ComboBox<String> boxP1 = new ComboBox<>(FXCollections.observableArrayList("10%","20%","30%","40%","50%","60%","70%","80%","90%","100%"));
    private final ComboBox<String> boxP2 = new ComboBox<>(FXCollections.observableArrayList("10%","20%","30%","40%","50%","60%","70%","80%","90%","100%"));

    private final Text textN1 = new Text("Время появления рабочих");
    private final Text textN2 = new Text("Время появления военных");
    private final Text textLifeTimeWork = new Text("Время жизни рабочих");
    private final Text textLifeTimeWar= new Text("Время жизни рабочих");
    private final Text textP1 = new Text("Верояность появления рабочих");
    private final Text textP2 = new Text("Вероятность появления военных");

    private final Button dalee = new Button("Далее");

    private final Pane menuRoot = new Pane(textFieldN1, textFieldN2,textFieldLifeTimeWar, textFieldLifeTimeWork,
            boxP1, boxP2, textN1, textN2, textP1,textLifeTimeWar, textLifeTimeWork, textP2, dalee);

    private final Scene menu;
    private static volatile MainMenu instance;

    private MainMenu(int W, int H) {
        textFieldN1.relocate(100, 100);
        textFieldN2.relocate(100, 300);
        textFieldLifeTimeWork.relocate(300, 100);
        textFieldLifeTimeWar.relocate(300, 300);
        boxP1.relocate(500, 100);
        boxP2.relocate(500, 300);

        textN1.relocate(100, 80);
        textN2.relocate(100, 280);
        textLifeTimeWar.relocate(300, 80);
        textLifeTimeWork.relocate(300, 280);
        textP1.relocate(500, 80);
        textP2.relocate(500, 280);

        dalee.relocate(200, 400);

        menu = new Scene(menuRoot, W, H);
    }

    public Scene getScene() {
        return menu;
    }

    public Button getDalee() {
        return dalee;
    }

    public TextField getN1() {
        return textFieldN1;
    }

    public TextField getLifeTimeWar() {
        return textFieldLifeTimeWar;
    }

    public TextField getLifeTimeWork() {
        return textFieldLifeTimeWork;
    }

    public ComboBox<String> getP2() {
        return boxP2;
    }

    public ComboBox<String> getP1() {
        return boxP1;
    }

    public TextField getN2() {
        return textFieldN2;
    }

    public boolean isInputTrue() {
        return textFieldN1.getText().chars().allMatch(Character::isDigit) &
                textFieldN2.getText().chars().allMatch(Character::isDigit) &
                textFieldLifeTimeWar.getText().chars().allMatch(Character::isDigit) &
                textFieldLifeTimeWork.getText().chars().allMatch(Character::isDigit);
    }

    public void showError() {
        Label error = new Label("Неверный формат данных");
        error.relocate(100, 500);
        error.setTextFill(Color.rgb(255,0,0));
        menuRoot.getChildren().add(error);
    }

    public static MainMenu getInstance() {
        MainMenu localInstance = instance;
        if (localInstance == null) {
            synchronized (MainMenu.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new MainMenu(
                            Habitat.getInstance().getWIDTH(),
                            Habitat.getInstance().getHEIGHT());
                }
            }
        }
        return localInstance;
    }
}
