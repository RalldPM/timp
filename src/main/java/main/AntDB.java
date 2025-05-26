package main;

import javafx.scene.control.Button;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AntDB {
    private final String URL = "jdbc:postgresql://localhost:5432/ants";
    private final String user = "postgres";
    private final String password = "1234";
    private Connection connection;

    private final Button saveBtn = new Button("Сохранить в\nбазу данных");
    private final Button loadBtn = new Button("Загрузить из\nбазы данных");
    private final Button saveWar = new Button("Сохранить\nв базу только\nвоенных");
    private final Button saveWork = new Button("Сохранить\nв базу только\nрабочих");

    private static volatile AntDB instance;

    private AntDB() {
        saveBtn.relocate(120, 605);
        saveBtn.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 12px; -fx-font-weight: bold;");
        loadBtn.relocate(20, 605);
        loadBtn.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 12px; -fx-font-weight: bold;");
        saveWar.relocate(20, 650);
        saveWar.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 12px; -fx-font-weight: bold;");
        saveWork.relocate(120, 650);
        saveWork.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 12px; -fx-font-weight: bold;");


        saveBtn.setOnAction(e -> {
            try{
                saveFromDB();
            } catch (SQLException ex) {
                System.err.println("Ошибка: " + ex.getMessage());
            }
        });

        saveWork.setOnAction(e -> {
            try {
                saveWorkersFromDB();
            } catch (SQLException ex) {
                System.err.println("Ошибка: " + ex.getMessage());
            }
        });

        saveWar.setOnAction(e -> {
            try {
                saveWarriorsFromDB();
            } catch (SQLException ex) {
                System.err.println("Ошибка: " + ex.getMessage());
            }
        });

        try {
            connection = DriverManager.getConnection(URL, user, password);
            createTable();
        } catch (SQLException ex) {
            System.err.println("Ошибка: " + ex.getMessage());
        }
    }

    private void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS ants (" +
                "id INTEGER, " +
                "X INTEGER, " +
                "Y INTEGER, " +
                "BornMoment INTEGER);";
        Statement statement = connection.createStatement();
        statement.execute(sql);
    }

    public void saveFromDB() throws SQLException {
        Statement statementDeleteAll = connection.createStatement();
        statementDeleteAll.execute("DELETE FROM ants;");

        String sql = "INSERT INTO ants (id, x, y, bornmoment) VALUES (?, ?, ?, ?);";
        for (AbstractAnt ant : AntCollection.ants().antVector()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, ant.getId());
            statement.setInt(2, (int) ant.getVisualObject().getCenterX());
            statement.setInt(3, (int) ant.getVisualObject().getCenterY());
            statement.setInt(4, ant.getBornMoment() - SpawnTimer.getInstance().getTime());
            statement.executeUpdate();
        }
    }

    public void loadIntoDB() throws SQLException {
        String sql = "SELECT * FROM ants";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            int posX = resultSet.getInt("x");
            int posY = resultSet.getInt("y");
            int bornMoment = resultSet.getInt("bornmoment");

            AbstractAnt newAnt;
            if (id / 10000 == 1)
                newAnt = new WorkerAnt(posX,posY,SpawnTimer.getInstance(),id);
            else
                newAnt = new WarriorAnt(posX,posY,SpawnTimer.getInstance(),id);
            newAnt.setBornMoment(SpawnTimer.getInstance().getTime() + bornMoment);

            AntCollection.ants().antVector().add(newAnt);
            AntCollection.ants().antSet().add(newAnt.getId());
            AntCollection.ants().antMap().put(newAnt.getId(), newAnt.getBornMoment());
        }
    }

    public void saveWorkersFromDB() throws SQLException {
        Statement statementDeleteAll = connection.createStatement();
        statementDeleteAll.execute("DELETE FROM ants;");

        String sql = "INSERT INTO ants (id, x, y, bornmoment) VALUES (?, ?, ?, ?);";
        for (AbstractAnt ant : AntCollection.ants().antVector()) {
            if (ant.getId() / 10000 == 1) {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, ant.getId());
                statement.setInt(2, (int) ant.getVisualObject().getCenterX());
                statement.setInt(3, (int) ant.getVisualObject().getCenterY());
                statement.setInt(4, ant.getBornMoment() - SpawnTimer.getInstance().getTime());
                statement.executeUpdate();
            }
        }
    }

    public void saveWarriorsFromDB() throws SQLException {
        Statement statementDeleteAll = connection.createStatement();
        statementDeleteAll.execute("DELETE FROM ants;");

        String sql = "INSERT INTO ants (id, x, y, bornmoment) VALUES (?, ?, ?, ?);";
        for (AbstractAnt ant : AntCollection.ants().antVector()) {
            if (ant.getId() / 10000 == 2) {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, ant.getId());
                statement.setInt(2, (int) ant.getVisualObject().getCenterX());
                statement.setInt(3, (int) ant.getVisualObject().getCenterY());
                statement.setInt(4, ant.getBornMoment() - SpawnTimer.getInstance().getTime());
                statement.executeUpdate();
            }
        }
    }

    public Button getSaveBtn() {
        return saveBtn;
    }

    public Button getLoadBtn() {
        return loadBtn;
    }

    public Button getSaveWar() {
        return saveWar;
    }

    public Button getSaveWork() {
        return saveWork;
    }

    public static AntDB getInstance() {
        AntDB localInstance = instance;
        if (localInstance == null) {
            synchronized (AntDB.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new AntDB();
                }
            }
        }
        return localInstance;
    }
}
