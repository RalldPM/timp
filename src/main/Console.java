package main;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class Console {
    private final Button consoleButton = new Button("Консоль");
    private final TextArea consoleTextArea = new TextArea();
    private final Pane consolePane = new Pane(consoleTextArea);
    private final Scene consoleScene = new Scene(consolePane);
    private final Stage consoleStage = new Stage();

    private final PipedOutputStream outputStream = new PipedOutputStream();
    private final PrintWriter writer = new PrintWriter(outputStream, true);

    private volatile static Console instance;

    private Console() {
        consoleTextArea.setPrefColumnCount(35);
        consoleTextArea.setPrefRowCount(45);

        consoleButton.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");
        consoleButton.relocate(40, 560);

        consoleStage.setScene(consoleScene);
        consoleStage.initModality(Modality.NONE);
        consoleStage.setResizable(false);
        consoleStage.setTitle("Консоль");

        consoleButton.setOnAction(e -> {
            consoleButton.setDisable(true);
            consoleStage.showAndWait();
        });

        consoleStage.setOnCloseRequest(e -> {
            consoleButton.setDisable(false);
        });

        consoleTextArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String[] strings = consoleTextArea.getText().split("\n");
                if (strings.length != 0) {
                    writer.println(strings[strings.length - 1]);
                }
            }
        });
    }

    public Button getConsoleButton() {
        return consoleButton;
    }

    public TextArea getConsoleTextArea() {
        return consoleTextArea;
    }

    public PipedOutputStream getOutputStream() {
        return outputStream;
    }

    public static Console getInstance() {
        Console localInstance = instance;
        if (localInstance == null) {
            synchronized (Console.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Console();
                }
            }
        }
        return localInstance;
    }
}
