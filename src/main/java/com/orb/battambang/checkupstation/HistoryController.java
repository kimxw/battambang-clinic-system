package com.orb.battambang.checkupstation;

import com.orb.battambang.MainApp;
import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.util.Labels;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class HistoryController extends CheckupMenuController implements Initializable {

    @FXML
    private Label queueSelectLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label sexLabel;
    @FXML
    private Label phoneNumberLabel;
    @FXML
    private Label status1Label;
    @FXML
    private Label status2Label;
    @FXML
    private Label status3Label;
    @FXML
    private Label status4Label;
    @FXML
    private Rectangle status1Rectangle;
    @FXML
    private Rectangle status2Rectangle;
    @FXML
    private Rectangle status3Rectangle;
    @FXML
    private Rectangle status4Rectangle;
    @FXML
    private Button editButton;
    @FXML
    private Button searchButton;
    @FXML
    private TextField queueNumberTextField;
    @FXML
    private Pane particularsPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize any necessary data here
        // Add a listener to the text property of the queueNumberTextField
        queueNumberTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Hide particularsPane when typing starts
                if (newValue != null && !newValue.isEmpty()) {
                    particularsPane.setVisible(false);
                }
            }
        });

        particularsPane.setVisible(false); // Initially hide the particularsPane
    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a queue number.", false);
        } else {
            updateParticularsPane(Integer.parseInt(queueNumberTextField.getText()));
            particularsPane.setVisible(true);
        }
    }

    public void updateParticularsPane(int queueNumber) {
        String patientQuery = "SELECT * FROM patientQueueTable WHERE queueNumber = " + queueNumber;
        String bmiRecordQuery = "SELECT * FROM heightAndWeightTable WHERE queueNumber = " + queueNumber;
        String snellensRecordQuery = "SELECT * FROM snellensTestTable WHERE queueNumber = " + queueNumber;
        // String hearingRecordQuery = "";
        // String historyRecordQuery = "";

        try {
            Statement statement = DatabaseConnection.connection.createStatement();

            // Fetch patient details
            ResultSet patientResultSet = statement.executeQuery(patientQuery);
            if (patientResultSet.next()) {
                String name = patientResultSet.getString("name");
                int age = patientResultSet.getInt("age");
                String sex = patientResultSet.getString("sex");
                String phoneNumber = patientResultSet.getString("phoneNumber");

                nameLabel.setText(name);
                ageLabel.setText(String.valueOf(age));
                sexLabel.setText(sex);
                phoneNumberLabel.setText(phoneNumber);
            } else {
                nameLabel.setText("");
                ageLabel.setText("");
                sexLabel.setText("");
                phoneNumberLabel.setText("");
                Labels.showMessageLabel(queueSelectLabel, "Patient does not exist", false);
                status1Rectangle.setStyle("-fx-fill: #707070;");
                status1Label.setText(" Not found");
                status2Rectangle.setStyle("-fx-fill: #707070;");
                status2Label.setText(" Not found");
                status3Rectangle.setStyle("-fx-fill: #707070;");
                status3Label.setText(" Not found");
                status4Rectangle.setStyle("-fx-fill: #707070;");
                status4Label.setText(" Not found");
                return;
            }

            // update record labels
            ResultSet bmiResultSet = statement.executeQuery(bmiRecordQuery);
            if (bmiResultSet.next()) {
                status1Rectangle.setStyle("-fx-fill: #9dd895;");
                status1Label.setText(" Complete");
            } else {
                status1Rectangle.setStyle("-fx-fill: #fa8072;");
                status1Label.setText("Incomplete");
            }

            ResultSet snellensResultSet = statement.executeQuery(snellensRecordQuery);
            if (snellensResultSet.next()) {
                status2Rectangle.setStyle("-fx-fill: #9dd895;");
                status2Label.setText(" Complete");
            } else {
                status2Rectangle.setStyle("-fx-fill: #fa8072;");
                status2Label.setText("Incomplete");
            }

            // ResultSet hearingResultSet = statement.executeQuery(hearingRecordQuery);
            // Update status based on hearingResultSet

            // ResultSet historyResultSet = statement.executeQuery(historyRecordQuery);
            // Update status based on historyResultSet

            // Close the statement
            statement.close();
        } catch (SQLException exc) {
            exc.printStackTrace();
            Labels.showMessageLabel(queueSelectLabel, "Database error occurred", false);
        }
    }

    void loadFXMLInNewWindow(String fxmlFile, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(fxmlFile));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            Stage currentStage = (Stage) editButton.getScene().getWindow();
            stage.initOwner(currentStage);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void editButtonOnAction(ActionEvent e) {
        loadFXMLInNewWindow("edit-history.fxml", "Edit history");
    }
}