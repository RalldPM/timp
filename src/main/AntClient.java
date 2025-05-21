package main;

import com.sun.source.tree.Tree;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class AntClient {
    private static Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private int id;
    private Set<Integer> idSet = Collections.synchronizedSet(new TreeSet<>());
    private final Button viewIDSetButton = new Button("список\nклиентов");
    private final ListView<Integer> listID = new ListView<>();
    private final Text yourIdText = new Text();

    private volatile static AntClient instance;

    private AntClient() {
        try {
            clientSocket = new Socket("localhost", 12345);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            this.id = in.readInt();
            this.idSet = (Set<Integer>) in.readObject();

            listID.setItems(FXCollections.observableArrayList(idSet));
        } catch (Exception ex) {
            System.err.println("Ошибка: " + ex.getMessage());
        }
        viewIDSetButton.relocate(180, 550);
        viewIDSetButton.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");

        listID.setVisible(false);
        listID.setEditable(false);
        listID.setPrefSize(50,150);
        listID.relocate(1000, 10);

        viewIDSetButton.setOnAction(e -> {
            listID.setVisible(!listID.isVisible());
        });

        yourIdText.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-font-weight: bold;");
        yourIdText.relocate(200, 310);
        yourIdText.setText("id: " + String.valueOf(id));
    }

    public void closeConnection() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (clientSocket != null) clientSocket.close();
    }

    public Button getViewIDSetButton() {return viewIDSetButton;}

    public ListView<Integer> getListID() {return listID;}

    public int getId() {return id;}

    public Set<Integer> getIdSet() {return idSet;}

    public ObjectOutputStream getOut() { return out; }

    public ObjectInputStream getIn() {return in;}

    public Text getYourIdText() {return yourIdText;}

    public static AntClient getInstance() {
        AntClient localInstance = instance;
        if (localInstance == null) {
            synchronized (AntClient.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new AntClient();
                }
            }
        }
        return localInstance;
    }
}
