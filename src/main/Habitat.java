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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PipedInputStream;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Habitat extends Application {

    private long N1 = 1;    // время рождения рабочих муравьев
    private long N2 = 1;    // время рождения военных муравьев
    private int P1 = 100;   // вероятность рождения рабочих муравьев
    private int P2 = 100;   // вероятность рождения военных муравьев

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

        LoadSave.getInstance().getLoadBtn().setOnAction(e -> {
            stopSpawn();
            File file = LoadSave.getInstance().getFileChooser().showOpenDialog(stage);
            if (file != null) {
                clearCollections();

                SpawnControls.getInstance().getStartBtn().setDisable(true);
                SpawnControls.getInstance().getStopBtn().setDisable(false);
                Info.getInstance().getInfoBtn().setDisable(false);

                try(FileReader reader = new FileReader(file)) {
                    StringBuilder antsText = new StringBuilder();
                    for (int c; (c = reader.read()) != -1;) {
                        antsText.append((char) c);
                    }
                    String[] antsArray = antsText.toString().split("\n");
                    Platform.runLater(() -> {
                        for (String antString : antsArray) {
                            String[] AntStringProperties = antString.split(" ");
                            AbstractAnt ant;
                            if (Integer.parseInt(AntStringProperties[0]) / 10000 == 1) {
                                ant = new WorkerAnt(
                                        Double.parseDouble(AntStringProperties[2]),
                                        Double.parseDouble(AntStringProperties[3]),
                                        st, Integer.parseInt(AntStringProperties[0]));
                            }
                            else {
                                ant = new WarriorAnt(
                                        Double.parseDouble(AntStringProperties[2]),
                                        Double.parseDouble(AntStringProperties[3]),
                                        st, Integer.parseInt(AntStringProperties[0]));
                            }
                            ant.setBornMoment(Integer.parseInt(AntStringProperties[1]));
                            AntCollection.ants().antVector().add(ant);
                            AntCollection.ants().antSet().add(ant.getId());
                            AntCollection.ants().antMap().put(ant.getId(), ant.getBornMoment());
                            antRoot.getChildren().add(ant.getVisualObject());
                        }
                    });
                }
                catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                SpawnTimer.getInstance().start();
            }
            else {
                SpawnTimer.getInstance().continueTimer();
            }
            startSpawn();
        });

        LoadSave.getInstance().getSaveBtn().setOnAction(e -> {
            stopSpawn();
            File file = LoadSave.getInstance().getFileChooser().showSaveDialog(stage);
            if (file != null) {
                try(FileWriter writer = new FileWriter(file)) {
                    for (AbstractAnt ant : AntCollection.ants().antVector()) {
                        writer.write(ant.getId() + " " + (ant.getBornMoment() - st.getTime()) + " " + ant.getVisualObject().getCenterX()
                                + " " + ant.getVisualObject().getCenterY() + "\n");
                    }
                }
                catch(Exception ex) {
                    System.out.println(ex.getMessage());
                }

            }
            if (SpawnControls.getInstance().getStartBtn().isDisabled()) {
                startSpawn();
                SpawnTimer.getInstance().continueTimer();
            }
        });

        try {
            PipedInputStream inputStream = new PipedInputStream(Console.getInstance().getOutputStream());
            Scanner scanner = new Scanner(inputStream);

            Thread consoleThread = new Thread(() -> {
                while (scanner.hasNextLine()) {
                    String command = scanner.nextLine();
                    Platform.runLater(() -> {
                        if (command.equalsIgnoreCase("start")) {
                            if (!SpawnControls.getInstance().getStartBtn().isDisabled()) {
                                startSpawn();
                                clearCollections();
                                SpawnTimer.getInstance().start();

                                SpawnControls.getInstance().getStartBtn().setDisable(true);
                                SpawnControls.getInstance().getStopBtn().setDisable(false);
                                Info.getInstance().getInfoBtn().setDisable(false);

                                Console.getInstance().getConsoleTextArea().appendText("Муравьи начали спавниться\n");
                            } else {
                                Console.getInstance().getConsoleTextArea().appendText("Муравьи уже спавнятся\n");
                            }
                        } else if (command.equalsIgnoreCase("end")) {
                            if (!SpawnControls.getInstance().getStopBtn().isDisabled()) {
                                SpawnControls.getInstance().getStartBtn().setDisable(false);
                                SpawnControls.getInstance().getStopBtn().setDisable(true);
                                Info.getInstance().getInfoBtn().setDisable(true);

                                stopSpawn();

                                if (Info.getInstance().getToggleInfo().isSelected()) {
                                    Info.getInstance().showStats(AntCollection.ants().antVector(),
                                            SpawnTimer.getInstance().getTimerText().getText());
                                }
                                Console.getInstance().getConsoleTextArea().appendText("Муравьи перестали спавниться\n");
                            } else {
                                Console.getInstance().getConsoleTextArea().appendText("Муравьи уже не спавнятся\n");
                            }
                        } else {
                            if (!(Console.getInstance().getConsoleTextArea().getText().charAt(
                                    Console.getInstance().getConsoleTextArea().getText().length() - 2) == '\n')) {
                                Console.getInstance().getConsoleTextArea().appendText("Неизвестная команда\n");
                            }
                        }
                    });
                }
            });

            consoleThread.setDaemon(true);
            consoleThread.start();
        }
        catch (IOException ex) {
            Console.getInstance().getConsoleTextArea().appendText("Ошибка: " + ex.getMessage() + "\n");
        }

        aiControls.getWarComboBox().setOnAction(e -> {
            warAI.setPriority(aiControls.getWarComboBox().getValue());
        });

        aiControls.getWorkComboBox().setOnAction(e -> {
            workAI.setPriority(aiControls.getWorkComboBox().getValue());
        });

        aiControls.getWarAIBox().setOnAction(e -> {
            synchronized (lockWar) {
                movingWar = !movingWar;
                if (movingWar & spawnControls.getStartBtn().isDisabled()) {
                    lockWar.notify();
                }
            }
        });

        aiControls.getWorkAIBox().setOnAction(e -> {
            synchronized (lockWork) {
                movingWork = !movingWork;
                if (movingWork & spawnControls.getStartBtn().isDisabled()) {
                    lockWork.notify();
                }
            }
        });

        spawnControls.getStartBtn().setOnAction(event -> {
            startSpawn();
            clearCollections();
            SpawnTimer.getInstance().start();

            spawnControls.getStartBtn().setDisable(true);
            spawnControls.getStopBtn().setDisable(false);
            info.getInfoBtn().setDisable(false);
        });

        spawnControls.getStopBtn().setOnAction(event -> {
            spawnControls.getStartBtn().setDisable(false);
            spawnControls.getStopBtn().setDisable(true);
            info.getInfoBtn().setDisable(true);

            stopSpawn();

            if (info.getToggleInfo().isSelected()) {
                info.showStats(AntCollection.ants().antVector(), st.getTimerText().getText());
            }
        });

        spawnControls.getExit().setOnAction(e -> {
            spawnControls.getStartBtn().setDisable(false);
            spawnControls.getStopBtn().setDisable(true);

            stage.setScene(menu.getScene());

            st.stop();
            stopAction();

            info.getInfoBtn().setDisable(true);
        });

        st.getTimerButton().setOnAction(event -> st.setTimerVisible());

        scene.setOnKeyPressed(event -> {
            switch(event.getCode()) {
                case B -> {
                    if (!spawnControls.getStartBtn().isDisabled()) {
                        startSpawn();
                        clearCollections();
                        SpawnTimer.getInstance().start();

                        spawnControls.getStartBtn().setDisable(true);
                        spawnControls.getStopBtn().setDisable(false);

                        info.getInfoBtn().setDisable(false);
                    }
                }
                case E -> {
                    if (!spawnControls.getStopBtn().isDisabled()) {
                        spawnControls.getStartBtn().setDisable(false);
                        spawnControls.getStopBtn().setDisable(true);

                        stopSpawn();

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
            startSpawn();
            SpawnTimer.getInstance().continueTimer();

            spawnControls.getStartBtn().setDisable(true);
            spawnControls.getStopBtn().setDisable(false);

            info.close();
            info.getInfoBtn().setDisable(false);
        });

        info.getInfoBtn().setOnAction(e -> {
            stopSpawn();
            info.showAntsInfo(AntCollection.ants().antMap());
        });

        info.getAntsStage().setOnCloseRequest(e -> {
            startSpawn();
            SpawnTimer.getInstance().continueTimer();
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
                aiControls.getWarComboBox(), aiControls.getWorkComboBox(),
                Console.getInstance().getConsoleButton(),
                LoadSave.getInstance().getLoadBtn(), LoadSave.getInstance().getSaveBtn());

        try (FileReader configReader = new FileReader("config.txt")) {
            StringBuilder configText = new StringBuilder();
            for (int c;(c = configReader.read()) != -1;) {
                configText.append((char)c);
            }
            String[] configVars = configText.toString().split("\n");

            menu.getN1().setText(configVars[0]);
            N1 = Integer.parseInt(configVars[0]);
            menu.getN2().setText(configVars[1]);
            N2 = Integer.parseInt(configVars[1]);

            menu.getP1().setValue(configVars[2] + "%");
            P1 = Integer.parseInt(configVars[2]);
            menu.getP2().setValue(configVars[3] + "%");
            P2 = Integer.parseInt(configVars[3]);

            menu.getLifeTimeWar().setText(configVars[4]);
            WarriorAnt.setLifeTime(Integer.parseInt(configVars[4]));
            menu.getLifeTimeWork().setText(configVars[5]);
            WorkerAnt.setLifeTime(Integer.parseInt(configVars[5]));

            if (st.getTimerText().isVisible() != Boolean.parseBoolean(configVars[6]))
                st.setTimerVisible();
            Info.getInstance().getToggleInfo().setSelected(Boolean.parseBoolean(configVars[7]));

            aiControls.getWarAIBox().setSelected(Boolean.parseBoolean(configVars[8]));
            aiControls.getWorkAIBox().setSelected(Boolean.parseBoolean(configVars[9]));

            aiControls.getWarR().setText(configVars[10]);
            WarriorAnt.setR(Double.parseDouble(configVars[10]));
            aiControls.getWarV().setText(configVars[11]);
            WarriorAnt.setV(Double.parseDouble(configVars[11]));
            aiControls.getWorkV().setText(configVars[12]);
            WorkerAnt.setV(Double.parseDouble(configVars[12]));

            warAI.setPriority(Integer.parseInt(configVars[13]));
            aiControls.getWarComboBox().setValue(Integer.parseInt(configVars[13]));
            workAI.setPriority(Integer.parseInt(configVars[14]));
            aiControls.getWorkComboBox().setValue(Integer.parseInt(configVars[14]));
        }
        catch (Exception ex) {
            System.out.println("Ошибка при чтении файла: " + ex.getMessage());
        }

        menu.getN1().setOnAction(e -> {
            if (menu.getN1().getText().chars().allMatch(Character::isDigit))
                N1 = Long.parseLong(menu.getN1().getText());
            else
                menu.showError();
        });

        menu.getN2().setOnAction(e -> {
            if (menu.getN2().getText().chars().allMatch(Character::isDigit))
                N2 = Long.parseLong(menu.getN2().getText());
            else
                menu.showError();
        });

        menu.getLifeTimeWork().setOnAction(e -> {
            if (menu.getLifeTimeWork().getText().chars().allMatch(Character::isDigit))
                WorkerAnt.setLifeTime(Integer.parseInt(menu.getLifeTimeWork().getText()));
            else
                menu.showError();
        });

        menu.getLifeTimeWar().setOnAction(e -> {
            if (menu.getLifeTimeWar().getText().chars().allMatch(Character::isDigit))
                WarriorAnt.setLifeTime(Integer.parseInt(menu.getLifeTimeWar().getText()));
            else
                menu.showError();
        });

        menu.getP1().setOnAction(e ->
            P1 = Integer.parseInt(menu.getP1().getValue().substring(0, menu.getP1().getValue().length() - 1)));

        menu.getP2().setOnAction(e ->
            P2 = Integer.parseInt(menu.getP2().getValue().substring(0, menu.getP2().getValue().length() - 1)));

        menu.getDalee().setOnAction(e -> stage.setScene(scene));

        stage.setResizable(false);
        stage.setScene(menu.getScene());
        stage.centerOnScreen();
        stage.setTitle("Муравьи");
        stage.show();
        stage.setOnCloseRequest(e -> {
            try (FileWriter configWrite = new FileWriter("config.txt")) {
                configWrite.write(N1 + "\n");
                configWrite.write(N2 + "\n");
                configWrite.write(P1 + "\n");
                configWrite.write(P2 + "\n");
                configWrite.write(WarriorAnt.getLifeTime() + "\n");
                configWrite.write(WorkerAnt.getLifeTime() + "\n");
                configWrite.write(st.getTimerText().isVisible() + "\n");
                configWrite.write(Info.getInstance().getToggleInfo().isSelected() + "\n");
                configWrite.write(movingWar + "\n");
                configWrite.write(movingWork + "\n");
                configWrite.write(WarriorAnt.getR() + "\n");
                configWrite.write(WarriorAnt.getV() + "\n");
                configWrite.write(WorkerAnt.getV() + "\n");
                configWrite.write(warAI.getPriority() + "\n");
                configWrite.write(workAI.getPriority() + "");
            }
            catch(Exception ex) {
                System.out.println("Ошибка при записи в файл: " + ex.getMessage());
            }
            System.exit(0);
        });
    }

    public void startSpawn() {
        synchronized (lockWar) {
            if (AIControls.getInstance().getWarAIBox().isSelected()) {
                lockWar.notify();
                movingWar = true;
            }
        }
        synchronized (lockWork) {
            if (AIControls.getInstance().getWorkAIBox().isSelected()) {
                lockWork.notify();
                movingWork = true;
            }
        }
        spawn(SpawnTimer.getInstance());
        killAnts(SpawnTimer.getInstance());
    }

    public void stopSpawn() {
        SpawnTimer.getInstance().stop();
        stopAction();

        movingWar = movingWork = false;
    }

    public void clearCollections() {
        AntCollection.ants().antVector().clear();
        AntCollection.ants().antSet().clear();
        AntCollection.ants().antMap().clear();
        antRoot.getChildren().clear();
    }

    private void spawn(SpawnTimer st) {

        Random random = new Random();

        spawnWork = executor.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                if (random.nextInt(0, 101) < P1) {
                    AbstractAnt newAnt = new WorkerAnt(
                            random.nextInt(312, WIDTH - 50),
                            random.nextInt(10, HEIGHT), st, getUniqueID(Professions.WORKER));
                    AntCollection.ants().antVector().add(newAnt);
                    AntCollection.ants().antSet().add(newAnt.getId());
                    AntCollection.ants().antMap().put(newAnt.getId(), newAnt.getBornMoment());
                    antRoot.getChildren().add(newAnt.getVisualObject());
                }
            });
        }, N1, N1, TimeUnit.SECONDS);

        spawnWar = executor.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                if (random.nextInt(0, 101) < P2) {
                    AbstractAnt newAnt = new WarriorAnt(
                            random.nextInt(312, WIDTH - 50),
                            random.nextInt(10, HEIGHT), st, getUniqueID(Professions.WARRIOR));
                    AntCollection.ants().antVector().add(newAnt);
                    AntCollection.ants().antSet().add(newAnt.getId());
                    AntCollection.ants().antMap().put(newAnt.getId(), newAnt.getBornMoment());
                    antRoot.getChildren().add(newAnt.getVisualObject());
                }
            });
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
            Platform.runLater(() -> {
                Vector<AbstractAnt> deadAnts = new Vector<>();
                for (AbstractAnt ant : AntCollection.ants().antVector()) {
                    int lifeTime = (ant instanceof WarriorAnt)
                            ? WarriorAnt.getLifeTime()
                            : WorkerAnt.getLifeTime();
                    if (st.getTime() - ant.getBornMoment() >= lifeTime * 100) {
                        antRoot.getChildren().remove(ant.getVisualObject());
                        AntCollection.ants().antSet().remove(ant.getId());
                        AntCollection.ants().antMap().remove(ant.getId());
                        deadAnts.add(ant);
                    }
                }
                AntCollection.ants().antVector().removeAll(deadAnts);
            });
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void stopAction() {

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

    public static void main(String[] args) {
        launch();
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