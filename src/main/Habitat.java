package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.util.Random;
import java.util.Vector;
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
    private ScheduledFuture<?> despawn;

    private final Pane antRoot = new Pane();

    private final AntCollection ants = new AntCollection();

    public void start(Stage stage) {

        Pane mainRoot = new Pane();
        Pane controls = new Pane();
        mainRoot.getChildren().addAll(antRoot, controls);

        MainMenu menu = new MainMenu(WIDTH, HEIGHT);
        Scene scene = new Scene(mainRoot, WIDTH, HEIGHT);

        executor = Executors.newScheduledThreadPool(3);

        Shape panel = new Rectangle(3, 3, 300, HEIGHT - 6);
        panel.setFill(Color.WHITE);
        panel.setStroke(Color.GOLD);
        panel.setStrokeWidth(6);

        Label copyRight = new Label("CompChair and ebanat777©");
        copyRight.setTextFill(Color.rgb(230, 230, 230));
        copyRight.relocate(40, 690);

        SpawnControls spawnControls = new SpawnControls(40, 140);
        SpawnTimer st = new SpawnTimer(110, 40);
        Info info = new Info(40, 200, stage);

        spawnControls.getStartBtn().setOnAction(event -> {
            spawnControls.getStartBtn().setDisable(true);
            spawnControls.getStopBtn().setDisable(false);
            info.getInfoBtn().setDisable(false);
            ants.antVector().clear();
            ants.antSet().clear();
            ants.antMap().clear();
            antRoot.getChildren().clear();
            st.start(executor);
            startSpawn(st);
            killAnts(st);
        });

        spawnControls.getStopBtn().setOnAction(event -> {
            st.stop();
            stopSpawn();
            spawnControls.getStartBtn().setDisable(false);
            spawnControls.getStopBtn().setDisable(true);
            info.getInfoBtn().setDisable(true);
            if (info.getToggleInfo().isSelected()) {
                info.showStats(ants.antVector(), st.getTimerText().getText());
            }
        });

        spawnControls.getExit().setOnAction(e -> {
            stage.setScene(menu.getScene());
            st.stop();
            stopSpawn();
            spawnControls.getStartBtn().setDisable(false);
            spawnControls.getStopBtn().setDisable(true);
            info.getInfoBtn().setDisable(true);
        });

        st.getTimerButton().setOnAction(event -> st.setTimerVisible());

        scene.setOnKeyPressed(event -> {
            switch(event.getCode()) {
                case B -> {
                    if (!spawnControls.getStartBtn().isDisabled()) {
                        spawnControls.getStartBtn().setDisable(true);
                        spawnControls.getStopBtn().setDisable(false);
                        info.getInfoBtn().setDisable(false);
                        ants.antVector().clear();
                        ants.antSet().clear();
                        ants.antMap().clear();
                        antRoot.getChildren().clear();
                        st.start(executor);
                        startSpawn(st);
                        killAnts(st);
                    }
                }
                case E -> {
                    if (!spawnControls.getStopBtn().isDisabled()) {
                        st.stop();
                        stopSpawn();
                        spawnControls.getStartBtn().setDisable(false);
                        spawnControls.getStopBtn().setDisable(true);
                        info.getInfoBtn().setDisable(true);
                        if (info.getToggleInfo().isSelected()) {
                            info.showStats(ants.antVector(), st.getTimerText().getText());
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
            startSpawn(st);
            killAnts(st);
            st.continueTimer(executor);
            spawnControls.getStartBtn().setDisable(true);
            spawnControls.getStopBtn().setDisable(false);
            info.getInfoBtn().setDisable(false);
        });

        info.getInfoBtn().setOnAction(e -> {
            st.stop();
            stopSpawn();
            info.showAntsInfo(ants.antMap());
        });

        info.getAntsStage().setOnCloseRequest(e -> {
            startSpawn(st);
            st.continueTimer(executor);
            killAnts(st);
        });

        controls.getChildren().addAll(
                panel,
                st.getTimerButton(),
                st.getTimerText(),
                spawnControls.getStartBtn(),
                spawnControls.getStopBtn(),
                spawnControls.getExit(),
                info.getToggleInfo(),
                info.getInfoBtn(),
                copyRight);

        menu.getDalee().setOnAction(e -> {
            if (menu.isInputTrue()) {
                if (!menu.getN1().getText().isEmpty())
                    N1 = Long.parseLong(menu.getN1().getText());
                if (!menu.getN2().getText().isEmpty())
                    N2 = Long.parseLong(menu.getN2().getText());
                if (!menu.getLifeTimeWar().getText().isEmpty())
                    WarriorAnt.setLifeTime(Integer.parseInt(menu.getLifeTimeWar().getText()));
                if (!menu.getLifeTimeWork().getText().isEmpty())
                    WorkerAnt.setLifeTime(Integer.parseInt(menu.getLifeTimeWork().getText()));
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

    private void startSpawn(SpawnTimer st) {

        Random random = new Random();

        spawnWork = executor.scheduleAtFixedRate(() -> {
            if (random.nextInt(0, 101) < P1) {
                AbstractAnt newAnt = new WorkerAnt(
                        random.nextInt(312, WIDTH - 50),
                        random.nextInt(10, HEIGHT), st, getUniqueID("Worker"));
                ants.antVector().add(newAnt);
                ants.antSet().add(newAnt.getId());
                ants.antMap().put(newAnt.getId(), newAnt.getBornMoment());
                Platform.runLater(() -> antRoot.getChildren().add(newAnt.getProfession()));
            }
        }, N1, N1, TimeUnit.SECONDS);

        spawnWar = executor.scheduleAtFixedRate(() -> {
            if (random.nextInt(0, 101) < P2) {
                AbstractAnt newAnt = new WarriorAnt(
                        random.nextInt(312, WIDTH - 50),
                        random.nextInt(10, HEIGHT), st, getUniqueID("Warrior"));
                ants.antVector().add(newAnt);
                ants.antSet().add(newAnt.getId());
                ants.antMap().put(newAnt.getId(), newAnt.getBornMoment());
                Platform.runLater(() -> antRoot.getChildren().add(newAnt.getProfession()));
            }
        }, N1, N2, TimeUnit.SECONDS);
    }

    private int getUniqueID(String type) {
        Random random = new Random();
        int id = random.nextInt(10000);
        while (ants.antSet().contains(id))
            id = random.nextInt(10000);
        if (type.equals("Worker"))
            return id + 10000;
        return id + 20000;
    }

    private void killAnts(SpawnTimer st) {
        despawn = executor.scheduleAtFixedRate(() -> {
            Vector<AbstractAnt> deadAnts = new Vector<>();
            for (AbstractAnt ant : ants.antVector()) {
                int lifeTime;
                if (ant instanceof WarriorAnt)
                    lifeTime = WarriorAnt.getLifeTime();
                else
                    lifeTime = WorkerAnt.getLifeTime();
                if (st.getTime() - ant.getBornMoment() >= lifeTime * 100) {
                    Platform.runLater(() -> {
                        antRoot.getChildren().remove(ant.getProfession());
                    });
                    deadAnts.add(ant);
                    ants.antSet().remove(ant.getId());
                    ants.antMap().remove(ant.getId());
                }
            }
            ants.antVector().removeAll(deadAnts);
        }, 0, 1, TimeUnit.MILLISECONDS);
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
        if (despawn != null) {
            despawn.cancel(true);
            despawn = null;
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