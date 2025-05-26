package main;

import javafx.scene.control.Button;

public class SpawnControls {

    private final Button startBtn = new Button("старт");
    private final Button stopBtn = new Button("стоп");
    private final Button exit = new Button("Выход");
    private static volatile SpawnControls instance;

    private SpawnControls(int X, int Y) {
        startBtn.relocate(X, Y);
        startBtn.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 24px; -fx-font-weight: bold;");

        stopBtn.relocate(X + 147, Y);
        stopBtn.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 24px; -fx-font-weight: bold;");
        stopBtn.setDisable(true);

        exit.relocate(220, 684);
        exit.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 17px; -fx-font-weight: bold;");
    }

    public Button getStartBtn() {
        return startBtn;
    }

    public Button getStopBtn() {
        return stopBtn;
    }

    public Button getExit() { return exit; }

    public static SpawnControls getInstance() {
        SpawnControls localInstance = instance;
        if (localInstance == null) {
            synchronized (SpawnControls.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SpawnControls(40, 140);
                }
            }
        }
        return localInstance;
    }
}
