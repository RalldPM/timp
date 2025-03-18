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

    private TextField textFieldN1 = new TextField();
    private TextField textFieldN2 = new TextField();
    private ComboBox<String> boxP1 = new ComboBox<>(FXCollections.observableArrayList("10%","20%","30%","40%","50%","60%","70%","80%","90%","100%"));
    private ComboBox<String> boxP2 = new ComboBox<>(FXCollections.observableArrayList("10%","20%","30%","40%","50%","60%","70%","80%","90%","100%"));

    private Text textN1 = new Text("Время появления рабочих");
    private Text textN2 = new Text("Время появления военных");
    private Text textP1 = new Text("Верояность появления рабочих");
    private Text textP2 = new Text("Вероятность появления военных");

    private Button dalee = new Button("Далее");

    private Pane menuRoot = new Pane(textFieldN1, textFieldN2, boxP1, boxP2,
            textN1, textN2, textP1, textP2, dalee);

    private Scene menu;

    public MainMenu(int W, int H) {
        textFieldN1.setTooltip(new Tooltip("Время появления рабочих"));
        textFieldN2.setTooltip(new Tooltip("Время появления военных"));
        boxP1.setTooltip(new Tooltip("Верояность появления рабочих"));
        boxP2.setTooltip(new Tooltip("Вероятность появления военных"));

        textFieldN1.relocate(100, 100);
        textFieldN2.relocate(100, 300);
        boxP1.relocate(300, 100);
        boxP2.relocate(300, 300);

        textN1.relocate(100, 80);
        textN2.relocate(300, 80);
        textP1.relocate(100, 280);
        textP2.relocate(300, 280);

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
                textFieldN2.getText().chars().allMatch(Character::isDigit);
    }

    public void showError() {
        Label error = new Label("Неверный формат данных");
        error.relocate(100, 500);
        error.setTextFill(Color.rgb(255,0,0));
        menuRoot.getChildren().add(error);
    }
}
