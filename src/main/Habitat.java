package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Habitat extends Application {

    private long N1 = 1;      // время рождения рабочих муравьев
    private long N2 = 1;      // время рождения военных муравьев
    private int P1 = 100;  // вероятность рождения рабочих муравьев
    private int P2 = 100;  // вероятность рождения военных муравьев

    private final int WIDTH = 1080; // ширина экрана
    private final int HEIGHT = 720; // высота экрана

    private ScheduledExecutorService executor;
    private ScheduledFuture<?> spawnWork;
    private ScheduledFuture<?> spawnWar;

    private Pane AntRoot = new Pane();

    private ArrayList<AbstractAnt> ants = new ArrayList<>();

    public void start(Stage stage) {

        Pane mainRoot = new Pane();
        Pane controls = new Pane();

        Scene scene = new Scene(mainRoot, WIDTH, HEIGHT);

        executor = Executors.newScheduledThreadPool(3);

        Shape panel = new Rectangle(3, 3, 300, HEIGHT - 6);
        panel.setFill(Color.WHITE);
        panel.setStroke(Color.GOLD);
        panel.setStrokeWidth(6);

        Label copyRight = new Label("CompChair and ebanat777©");
        copyRight.setTextFill(Color.rgb(230, 230, 230));
        copyRight.relocate(50, 680);

        SpawnControls spawnControls = new SpawnControls(40, 140);
        SpawnTimer st = new SpawnTimer(110, 40);
        Info info = new Info(40, 200, stage);

        spawnControls.getStartBtn().setOnAction(event -> {
            spawnControls.getStartBtn().setDisable(true);
            spawnControls.getStopBtn().setDisable(false);
            ants.clear();
            AntRoot.getChildren().clear();
            st.start(executor);
            startSpawn();
        });

        spawnControls.getStopBtn().setOnAction(event -> {
            st.stop();
            stopSpawn();
            spawnControls.getStartBtn().setDisable(false);
            spawnControls.getStopBtn().setDisable(true);

            if (info.getToggleInfo().isSelected()) {
                info.showStats(ants, st.getTimerText().getText());
            }
        });

        st.getTimerButton().setOnAction(event -> st.setTimerVisible());

        scene.setOnKeyPressed(event -> {
            switch(event.getCode()) {
                case B -> {
                    if (!spawnControls.getStartBtn().isDisabled()) {
                        spawnControls.getStartBtn().setDisable(true);
                        spawnControls.getStopBtn().setDisable(false);
                        ants.clear();
                        AntRoot.getChildren().clear();
                        st.start(executor);
                        startSpawn();
                    }
                }
                case E -> {
                    if (!spawnControls.getStopBtn().isDisabled()) {

                        st.stop();
                        stopSpawn();
                        spawnControls.getStartBtn().setDisable(false);
                        spawnControls.getStopBtn().setDisable(true);

                        if (info.getToggleInfo().isSelected()) {
                            info.showStats(ants, st.getTimerText().getText());
                        }
                    }
                }
                case T -> st.setTimerVisible();
                default -> {}
            }
        });

        info.getCloseButton().setOnAction(e -> {
            info.close();
        });

        info.getContinueButton().setOnAction(e -> {
            info.close();
            startSpawn();
            st.continueTimer(executor);
            spawnControls.getStartBtn().setDisable(true);
            spawnControls.getStopBtn().setDisable(false);
        });

        controls.getChildren().addAll(
                panel,
                st.getTimerButton(),
                st.getTimerText(),
                spawnControls.getStartBtn(),
                spawnControls.getStopBtn(),
                info.getToggleInfo(),
                copyRight);

        mainRoot.getChildren().addAll(AntRoot, controls);

        MainMenu menu = new MainMenu(WIDTH, HEIGHT);

        menu.getDalee().setOnAction(e -> {
            if (menu.isInputTrue()) {
                if (!menu.getN1().getText().isEmpty())
                    N1 = Long.parseLong(menu.getN1().getText());
                if (!menu.getN2().getText().isEmpty())
                    N2 = Long.parseLong(menu.getN2().getText());
                if (menu.getP1().getValue() != null)
                    P1 = Integer.parseInt(menu.getP1().getValue().substring(0, menu.getP1().getValue().length() - 1));
                if (menu.getP2().getValue() != null)
                    P2 = Integer.parseInt(menu.getP2().getValue().substring(0, menu.getP2().getValue().length() - 1));
                stage.setScene(scene);
            }
            else {
                menu.showError();
            }
        });

        stage.setResizable(false);
        stage.setScene(menu.getScene());
        stage.centerOnScreen();
        stage.setTitle("Муравьи");
        stage.show();
    }

    private void startSpawn() {

        Random random = new Random();
        spawnWork = executor.scheduleAtFixedRate(() -> {
            if (random.nextInt(0, 101) < P1) {
                AbstractAnt newAnt = new WorkerAnt(
                        random.nextInt(312, WIDTH - 50),
                        random.nextInt(10, HEIGHT));
                ants.add(newAnt);
                Platform.runLater(() -> AntRoot.getChildren().add(newAnt.getProfession()));
            }
        }, N1, N1, TimeUnit.SECONDS);

        spawnWar = executor.scheduleAtFixedRate(() -> {
            if (random.nextInt(0, 101) < P2) {
                AbstractAnt newAnt = new WarriorAnt(
                        random.nextInt(312, WIDTH - 50),
                        random.nextInt(10, HEIGHT));
                ants.add(newAnt);
                Platform.runLater(() -> AntRoot.getChildren().add(newAnt.getProfession()));
            }
        }, N1, N2, TimeUnit.SECONDS);
    }

    private void stopSpawn() {

        if (spawnWork != null) {
            spawnWork.cancel(true);
            spawnWork = null;
        }
        if (spawnWar != null) {
            spawnWar.cancel(true);
            spawnWar = null;
        }
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}