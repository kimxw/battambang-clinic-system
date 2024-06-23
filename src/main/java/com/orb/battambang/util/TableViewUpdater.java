package com.orb.battambang.util;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TableView;
import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.reception.Patient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

public class TableViewUpdater {

    private final ObservableList<Patient> patientObservableList;
    private final TableView<Patient> patientTableView;
    private final Connection connection = DatabaseConnection.connection;

    public TableViewUpdater(ObservableList<Patient> patientObservableList, TableView<Patient> patientTableView) {
        this.patientObservableList = patientObservableList;
        this.patientTableView = patientTableView;
        startPolling();
    }

    private void startPolling() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> updateTableView());
            }
        }, 0, 30000); // Poll every 30 seconds
    }

    private void updateTableView() {
        String query = "SELECT queueNumber, name, age, sex, phoneNumber, address FROM patientQueueTable";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            patientObservableList.clear(); // Clear the list before adding new items

            while (resultSet.next()) {
                Integer queueNo = resultSet.getInt("queueNumber");
                String name = resultSet.getString("name");
                Integer age = resultSet.getInt("age");
                String sexString = resultSet.getString("sex");
                Character sex = !sexString.isEmpty() ? sexString.charAt(0) : null;
                String phoneNumber = resultSet.getString("phoneNumber");
                String address = resultSet.getString("address");

                patientObservableList.add(new Patient(queueNo, name, age, sex, phoneNumber, address));
            }

            patientTableView.setItems(patientObservableList); // Update the TableView

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}