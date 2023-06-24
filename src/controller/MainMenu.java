package controller;

import java.io.IOException;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.Alerts;
import util.JDBC;

/**
 * Main Screen Class for Appointments
 * @author Seyed Hassanzadeh
 */

public class MainMenu implements Initializable {

    /**
	 * Logout Button 
	 */
    public Button logoutBtn;
	/**
	 * Appointment Button 
	 */
    public Button appointmentsBtn;
	/**
	 * Customers Button 
	 */
    public Button customersBtn;
	/**
	 * Exit Button 
	 */
    public Button exitBtn;
    /**
	 * Reports Button 
	 */
    public Button reportsBtn;

    Stage stage;
	Parent scene;
    
    /**
	 * Goes to Reports Screen
     * @param actionEvent The action event 
	 */
    public void onActionReports(ActionEvent actionEvent) throws IOException {
        buttonChanging(actionEvent, "/view/allReports.fxml");
    }
	/**
	 * Goes to Appointment Screen
     * @param actionEvent The action event 
	 */
    public void onActionAppointments(ActionEvent actionEvent) throws IOException {
        buttonChanging(actionEvent, "/view/appointmentScreen.fxml");
    }
	/**
	 * Changes to New Screen
     * @param actionEvent The action event
     * @param resourcesString The link to new screen 
	 */
    public void buttonChanging(ActionEvent actionEvent, String resourcesString) throws IOException {
        stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource(resourcesString));
        stage.setScene(new Scene(scene));
        stage.show();
    }
    /**
	 * Goes to Customers Table Screen
     * @param actionEvent The action event 
	 */
    public void onActionCustomers(ActionEvent actionEvent) throws IOException {
        buttonChanging(actionEvent, "/view/customersTable.fxml");
    }
    /**
	 * Goes to Login Screen
     * @param actionEvent The action event 
	 */
    public void onActionLogout(ActionEvent actionEvent) throws IOException {
        buttonChanging(actionEvent, "/view/sample.fxml");
    }
	/**
	 * Check appointments that are 15 minutes ahead of now for start, and 45 minutes ahead for end.
     * @param now now of the current time
     * @param end 15 minutes ahead of current time
	 */
    public void displayAppointmentReminder(Timestamp now, Timestamp end) throws SQLException {

        PreparedStatement appointmentWithin15Minutes = JDBC.getConnection().prepareStatement(
            "SELECT * " +
                "FROM appointments " +
                "INNER JOIN contacts ON appointments.Contact_ID = contacts.Contact_ID " +
                "WHERE Start BETWEEN ? AND ?");

        appointmentWithin15Minutes.setTimestamp(1, now);
        appointmentWithin15Minutes.setTimestamp(2, end);

        ResultSet appointmentResults = appointmentWithin15Minutes.executeQuery();

        if(appointmentResults.next())  {
            Alerts.informationAlert("Appointment Reminder",
                    ("Appointment ID = "+ appointmentResults.getInt(("Appointment_ID")) + " is within 15 minutes") ,
                    ("You have an upcoming appointment with " +
                        appointmentResults.getString("Contact_Name") +
                        " and is a " + appointmentResults.getString("Type") + " meeting. It starts at " +
                        appointmentResults.getTimestamp("Start").toLocalDateTime() + "."));
            } else {
            Alerts.informationAlert("Appointment Reminder", "No appointments in the next 15 minutes","");
        }
    }
    /**
	 * Initializes All Reminder
	 */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            getsAppointmentsIn15MinutesLocal();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
	 * Alerts user that there is an appointment in next 15 minutes.
	 */
    public void getsAppointmentsIn15MinutesLocal() throws SQLException {
        LocalDateTime localStart = LocalDateTime.now();
        LocalDateTime localEnd = LocalDateTime.now().plusMinutes(15);
        Timestamp now = Timestamp.valueOf(localStart);
        Timestamp end = Timestamp.valueOf(localEnd);
        displayAppointmentReminder(now, end);
    }
	/**
	 * Closes The Program
     * @param actionEvent The action event 
	 */
    public void onActionExit(ActionEvent actionEvent) {
        System.exit(0);
    }
}