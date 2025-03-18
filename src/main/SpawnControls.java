package main;

import javafx.scene.control.Button;

public class SpawnControls {

    private Button startBtn = new Button("старт");
    private Button stopBtn = new Button("стоп");

    public SpawnControls(int X, int Y) {
        startBtn.relocate(X, Y);
        startBtn.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 24px; -fx-font-weight: bold;");

        stopBtn.relocate(X + 147, Y);
        stopBtn.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 24px; -fx-font-weight: bold;");
        stopBtn.setDisable(true);
    }

    public Button getStartBtn() {
        return startBtn;
    }

    public Button getStopBtn() {
        return stopBtn;
    }
}
