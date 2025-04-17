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

import java.util.Iterator;
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
    private ScheduledFuture<?> despawn;

    private final Pane antRoot = new Pane();

    private volatile boolean movingWar = false;
    private volatile boolean movingWork = false;
    private final Object lockWar = new Object();
    private final Object lockWork = new Object();

    private static volatile Habitat instance;

    public void start(Stage stage) {

        Thread warAI = new Thread(new WarriorAI(lockWar, () -> movingWar));
        Thread workAI = new Thread(new WorkerAI(lockWork, () -> movingWork));
        warAI.setDaemon(true);
        workAI.setDaemon(true);
        warAI.start();
        workAI.start();

        Pane mainRoot = new Pane();
        Pane controls = new Pane();
        mainRoot.getChildren().addAll(antRoot, controls);

        MainMenu menu = MainMenu.getInstance();
        Scene scene = new Scene(mainRoot, WIDTH, HEIGHT);

        executor = Executors.newScheduledThreadPool(4);

        Shape panel = new Rectangle(3, 3, 300, HEIGHT - 6);
        panel.setFill(Color.WHITE);
        panel.setStroke(Color.GOLD);
        panel.setStrokeWidth(6);

        Label copyRight = new Label("CompChair and ebanat777©");
        copyRight.setTextFill(Color.rgb(230, 230, 230));
        copyRight.relocate(40, 690);

        SpawnControls spawnControls = SpawnControls.getInstance();
        SpawnTimer st = SpawnTimer.getInstance();
        AIControls aiControls =  AIControls.getInstance();
        Info info = Info.getInstance();
        info.setOwnerStage(stage);

        aiControls.getWarComboBox().setOnAction(e -> {
            warAI.setPriority(aiControls.getWarComboBox().getValue());
        });

        aiControls.getWorkComboBox().setOnAction(e -> {
            workAI.setPriority(aiControls.getWorkComboBox().getValue());
        });

        aiControls.getWarR().setOnAction(e -> {
            if (!aiControls.getWarR().getText().isEmpty())
                WarriorAnt.setR(Integer.parseInt(aiControls.getWarR().getText()));
        });

        aiControls.getWarV().setOnAction(e -> {
            if (!aiControls.getWarV().getText().isEmpty())
                WarriorAnt.setV(Integer.parseInt(aiControls.getWarV().getText()));
        });

        aiControls.getWorkV().setOnAction(e -> {
            if (!aiControls.getWorkV().getText().isEmpty())
                WorkerAnt.setV(Integer.parseInt(aiControls.getWorkV().getText()));
        });

        aiControls.getWarAIBox().setOnAction(e -> {
            synchronized (lockWar) {
                movingWar = !movingWar;
                if (movingWar) {
                    lockWar.notify();
                }
            }
        });

        aiControls.getWorkAIBox().setOnAction(e -> {
            synchronized (lockWork) {
                movingWork = !movingWork;
                if (movingWork) {
                    lockWork.notify();
                }
            }
        });

        spawnControls.getStartBtn().setOnAction(event -> {
            synchronized (lockWar) {
                if (aiControls.getWarAIBox().isSelected()) {
                    lockWar.notify();
                    movingWar = true;
                }
            }
            synchronized (lockWork) {
                if (aiControls.getWorkAIBox().isSelected()) {
                    lockWork.notify();
                    movingWork = true;
                }
            }

            spawnControls.getStartBtn().setDisable(true);
            spawnControls.getStopBtn().setDisable(false);
            info.getInfoBtn().setDisable(false);

            AntCollection.ants().antVector().clear();
            AntCollection.ants().antSet().clear();
            AntCollection.ants().antMap().clear();
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

            movingWar = movingWork = false;

            if (info.getToggleInfo().isSelected()) {
                info.showStats(AntCollection.ants().antVector(), st.getTimerText().getText());
            }
        });

        spawnControls.getExit().setOnAction(e -> {
            spawnControls.getStartBtn().setDisable(false);
            spawnControls.getStopBtn().setDisable(true);

            stage.setScene(menu.getScene());

            st.stop();
            stopSpawn();

            info.getInfoBtn().setDisable(true);
        });

        st.getTimerButton().setOnAction(event -> st.setTimerVisible());

        scene.setOnKeyPressed(event -> {
            switch(event.getCode()) {
                case B -> {
                    if (!spawnControls.getStartBtn().isDisabled()) {
                        synchronized (lockWar) {
                            if (aiControls.getWarAIBox().isSelected()) {
                                lockWar.notify();
                                movingWar = true;
                            }
                        }
                        synchronized (lockWork) {
                            if (aiControls.getWorkAIBox().isSelected()) {
                                lockWork.notify();
                                movingWork = true;
                            }
                        }

                        spawnControls.getStartBtn().setDisable(true);
                        spawnControls.getStopBtn().setDisable(false);

                        info.getInfoBtn().setDisable(false);

                        AntCollection.ants().antVector().clear();
                        AntCollection.ants().antSet().clear();
                        AntCollection.ants().antMap().clear();
                        antRoot.getChildren().clear();

                        st.start(executor);
                        startSpawn(st);
                        killAnts(st);
                    }
                }
                case E -> {
                    if (!spawnControls.getStopBtn().isDisabled()) {
                        spawnControls.getStartBtn().setDisable(false);
                        spawnControls.getStopBtn().setDisable(true);

                        st.stop();
                        stopSpawn();

                        movingWar = movingWork = false;

                        info.getInfoBtn().setDisable(true);
                        if (info.getToggleInfo().isSelected()) {
                            info.showStats(AntCollection.ants().antVector(), st.getTimerText().getText());
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
            synchronized (lockWar) {
                if (aiControls.getWarAIBox().isSelected()) {
                    lockWar.notify();
                    movingWar = true;
                }
            }
            synchronized (lockWork) {
                if (aiControls.getWorkAIBox().isSelected()) {
                    lockWork.notify();
                    movingWork = true;
                }
            }

            spawnControls.getStartBtn().setDisable(true);
            spawnControls.getStopBtn().setDisable(false);

            info.close();
            info.getInfoBtn().setDisable(false);

            startSpawn(st);
            killAnts(st);
            st.continueTimer(executor);
        });

        info.getInfoBtn().setOnAction(e -> {
            movingWar = movingWork = false;

            st.stop();
            stopSpawn();

            info.showAntsInfo(AntCollection.ants().antMap());
        });

        info.getAntsStage().setOnCloseRequest(e -> {
            synchronized (lockWar) {
                if (aiControls.getWarAIBox().isSelected()) {
                    lockWar.notify();
                    movingWar = true;
                }
            }
            synchronized (lockWork) {
                if (aiControls.getWorkAIBox().isSelected()) {
                    lockWork.notify();
                    movingWork = true;
                }
            }

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
                copyRight,
                aiControls.getWarAIBox(), aiControls.getWorkAIBox(),
                aiControls.getWarR(),aiControls.getWarV(),aiControls.getWorkV(),
                aiControls.getWarRText(), aiControls.getWarVText(), aiControls.getWorkVText(),
                aiControls.getWarComboBox(), aiControls.getWorkComboBox());

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
                        random.nextInt(10, HEIGHT), st, getUniqueID(Professions.WORKER));
                AntCollection.ants().antVector().add(newAnt);
                AntCollection.ants().antSet().add(newAnt.getId());
                AntCollection.ants().antMap().put(newAnt.getId(), newAnt.getBornMoment());
                Platform.runLater(() -> antRoot.getChildren().add(newAnt.getVisualObject()));
            }
        }, N1, N1, TimeUnit.SECONDS);

        spawnWar = executor.scheduleAtFixedRate(() -> {
            if (random.nextInt(0, 101) < P2) {
                AbstractAnt newAnt = new WarriorAnt(
                        random.nextInt(312, WIDTH - 50),
                        random.nextInt(10, HEIGHT), st, getUniqueID(Professions.WARRIOR));
                AntCollection.ants().antVector().add(newAnt);
                AntCollection.ants().antSet().add(newAnt.getId());
                AntCollection.ants().antMap().put(newAnt.getId(), newAnt.getBornMoment());
                Platform.runLater(() -> antRoot.getChildren().add(newAnt.getVisualObject()));
            }
        }, N1, N2, TimeUnit.SECONDS);
    }

    private int getUniqueID(Professions prof) {
        Random random = new Random();
        int id = random.nextInt(10000);
        while (AntCollection.ants().antSet().contains(id))
            id = random.nextInt(10000);
        if (prof == Professions.WORKER)
            return id + 10000;
        return id + 20000;
    }

    private void killAnts(SpawnTimer st) {
        despawn = executor.scheduleAtFixedRate(() -> {
            for (Iterator<AbstractAnt> it = AntCollection.ants().antVector().iterator(); it.hasNext();) {
                AbstractAnt ant = it.next();
                int lifeTime;
                if (ant instanceof WarriorAnt)
                    lifeTime = WarriorAnt.getLifeTime();
                else
                    lifeTime = WorkerAnt.getLifeTime();
                if (st.getTime() - ant.getBornMoment() >= lifeTime * 100) {
                    Platform.runLater(() -> {
                        antRoot.getChildren().remove(ant.getVisualObject());
                    });
                    AntCollection.ants().antSet().remove(ant.getId());
                    AntCollection.ants().antMap().remove(ant.getId());
                    it.remove();
                }
            }
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

    public boolean isMovingWork() {
        return movingWork;
    }

    public boolean isMovingWar() {
        return movingWar;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public static Habitat getInstance() {
        Habitat localInstance = instance;
        if (localInstance == null) {
            synchronized (Habitat.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Habitat();
                }
            }
        }
        return localInstance;
    }
}