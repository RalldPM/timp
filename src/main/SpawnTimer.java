package main;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SpawnTimer {

    private final Button timerButton = new Button();
    private ScheduledFuture<?> timer;
    private final Text timerText = new Text();
    private int time = 0;
    private boolean isTimerVisible = false;
    private static volatile SpawnTimer instance;

    private SpawnTimer(int X, int Y) {

        timerText.relocate(X,Y);
        timerText.setVisible(false);
        timerText.setScaleX(2);
        timerText.setScaleY(2);
        timerText.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 24px; -fx-font-weight: bold;");
        timerText.setText("00:00:00");

        timerButton.setText("Показать таймер");
        timerButton.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 24px; -fx-font-weight: bold;");
        timerButton.relocate(X - 70,Y + 40);
    }

    public Text getTimerText() {
        return timerText;
    }

    public Button getTimerButton() {return timerButton;}

    public int getTime() {return time;}

    public void continueTimer() {
        timer = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> Platform.runLater(() -> {

            if (time/100%60 < 10 & time%100 < 10)
                timerText.setText("0" + time/6000 + ":0" + time/100%60 + ":0" + time%100);
            else if (time%100 < 10)
                timerText.setText("0" + time/6000 + ":" + time/100%60 + ":0" + time%100);
            else if (time/100%60 < 10)
                timerText.setText("0" + time/6000 + ":0" + time/100%60 + ":" + time%100);
            else
                timerText.setText("0" + time/6000 + ":" + time/100%60 + ":" + time%100);
            time++;
        }), 0, 10, TimeUnit.MILLISECONDS);
    }

    public void start() {

        timerText.setText("00:00:00");
        time = 0;
        continueTimer();
    }

    public void stop() {

        if (timer != null) {
            timer.cancel(true);
            timer = null;
        }
    }

    public void setTimerVisible() {

        isTimerVisible = !isTimerVisible;
        timerText.setVisible(isTimerVisible);

        if (isTimerVisible)
            timerButton.setText("Скрыть таймер");
        else
            timerButton.setText("Показать таймер");
    }

    public static SpawnTimer getInstance() {
        SpawnTimer localInstance = instance;
        if (localInstance == null) {
            synchronized (SpawnTimer.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SpawnTimer(110, 40);
                }
            }
        }
        return localInstance;
    }
}
