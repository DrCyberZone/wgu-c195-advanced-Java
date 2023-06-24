package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import model.Alerts;
import model.Appointments;
import util.DataBaseQueries;
import util.JDBC;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


/**
 * Appointment Main Class and Methods to Sort by Week/Month and Methods to Verify Overlapping
 * @author Seyed Hassanzadeh
 */

public class AppointmentScreen implements Initializable {

    /**
	 * Current Week Observable List 
	 */
    private ObservableList<Appointments> filterByWeekList = FXCollections.observableArrayList();
	/**
	 * Current Month Observable List 
	 */
    private ObservableList<Appointments> filterByMonthList = FXCollections.observableArrayList();
    /**
	 * Selected Appointment
	 */
    public static Appointments highlightedAppointment;
    
	Parent scene;
    Stage stage;
    
	/**
	 * AppointmentID Column
	 */
    public TableColumn<Appointments, Integer> appointmentIdTblCol;
    /**
	 * Title Column
	 */
    public TableColumn<Appointments, String> titleTblCol;
    /**
	 * Description Column
	 */
    public TableColumn<Appointments, String> descriptionTblCol;
    /**
	 * Location Column
	 */
    public TableColumn<Appointments, String> locationTblCol;
    /**
	 * Contact Column
	 */
    public TableColumn<Appointments, String> contactTblCol;
    /**
	 * Type Column
	 */
    public TableColumn<Appointments, String> typeTblCol;
    /**
	 * Start Time Column
	 */
    public TableColumn<Appointments, LocalDateTime> startTblCol;
    /**
	 * End Time Column
	 */
    public TableColumn<Appointments, LocalDateTime> endTblCol;
    /**
	 * CustomerID Column
	 */
    public TableColumn<Appointments, Integer> customerIdTblCol;
    /**
	 * UserID Column
	 */
    public TableColumn<Appointments, Integer> userIdTblCol;
	/**
	 * Default App Button
	 */
    public Button defaultButton;
    /**
	 * MainMenu Button
	 */
    public Button mainMenuBtn;
    /**
	 * Appointment Table View
	 */
    public TableView<Appointments> appointmentTblView;
    /**
	 * Month Radio Button
	 */
    public RadioButton byMonthRadBtn;
    /**
	 * Week Radio Button
	 */
    public RadioButton byWeekRadBtn;
    /**
	 * Radio Button Group
	 */
    public ToggleGroup appointmentRadBtnTglGrp;
    /**
	 * Add Button
	 */
    public Button addAppointmentBtn;
    /**
	 * Delete Button
	 */
    public Button deleteAppointmentBtn;
    /**
	 * Modify Button
	 */
    public Button modifyAppointmentBtn;

    /**
	 * Goes to Modify Screen
     * @param actionEvent Handles the event when the button is called
	 */
    public void onActionModifyAppointment(ActionEvent actionEvent) throws IOException {

        highlightedAppointment = appointmentTblView.getSelectionModel().getSelectedItem();

        if(highlightedAppointment == null) {
            Alert alertForModify = new Alert(Alert.AlertType.ERROR);
            alertForModify.setHeaderText("No appointment selected");
            alertForModify.setContentText("Please select an appointment to modify");
            alertForModify.showAndWait();
        } else {
            buttonChanging(actionEvent, "/view/modifyAppointment.fxml");
        }
    }

	/**
	 * Filters Appointments by Current Month
     * @param actionEvent Radio button is selected
	 */
    public void onActionFilerByMonth(ActionEvent actionEvent) throws SQLException {
        filterByMonthList.clear();
        filterByWeek().clear();
        appointmentTblView.setItems(filterByMonth());
    }

	/**
	 * Populates Appointment Table
     * Lambda expression used to populate contacts name
	 */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Appointments.getGetAllAppointments().clear();
            appointmentTblView.setItems( Appointments.getGetAllAppointments());
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        appointmentIdTblCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        titleTblCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionTblCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationTblCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactTblCol.setCellValueFactory(appointment -> new SimpleStringProperty(appointment.getValue().getContactName()));
        typeTblCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        startTblCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endTblCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerIdTblCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        userIdTblCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
    }

	/**
	 * Filters Appointments by Week
     * @return Returns listed weekly appointments
	 */
    public ObservableList<Appointments> filterByWeek() throws SQLException {


        Statement weeklyAppointments = JDBC.getConnection().createStatement();
        String filterByWeekSql =
                "SELECT * " +
                "FROM appointments " +
                "INNER JOIN contacts ON appointments.Contact_ID = contacts.Contact_ID " +
                "WHERE DATE(Start) = DATE(NOW()) " +
                "OR Start >= NOW() " +
                "AND  Start < DATE_ADD(CURRENT_DATE(), interval 7 day)";

        ResultSet filterResults = weeklyAppointments.executeQuery(filterByWeekSql);

        while(filterResults.next()) {
            Appointments appointments = new Appointments(
                    filterResults.getInt("Appointment_ID"),
                    filterResults.getString("Title"),
                    filterResults.getString("Description"),
                    filterResults.getString("Location"),
                    filterResults.getString("Contact_Name"),
                    filterResults.getString("Type"),
                    filterResults.getTimestamp("Start").toLocalDateTime(),
                    filterResults.getTimestamp("End").toLocalDateTime(),
                    filterResults.getInt("Customer_ID"),
                    filterResults.getInt("User_ID"));
            filterByWeekList.add(appointments);
        }
        return filterByWeekList;
    }

	/**
	 * Filters Appointments by Month
     * @return Returns listed monthly appointments
	 */
    public ObservableList<Appointments> filterByMonth() throws SQLException {
        Month currentMonth = LocalDateTime.now().getMonth();
        String month = currentMonth.toString();
        int monthId = monthSelectionToID(month);

        Statement monthlyAppointments = JDBC.getConnection().createStatement();
        String filterByMonthSql =
                "SELECT * " +
                "FROM appointments " +
                "INNER JOIN contacts ON appointments.Contact_ID = contacts.Contact_ID " +
                "WHERE month(Start)=" + monthId;

        ResultSet filterResults = monthlyAppointments.executeQuery(filterByMonthSql);

        while(filterResults.next()) {
            Appointments appointments = new Appointments(
                    filterResults.getInt("Appointment_ID"),
                    filterResults.getString("Title"),
                    filterResults.getString("Description"),
                    filterResults.getString("Location"),
                    filterResults.getString("Contact_Name"),
                    filterResults.getString("Type"),
                    filterResults.getTimestamp("Start").toLocalDateTime(),
                    filterResults.getTimestamp("End").toLocalDateTime(),
                    filterResults.getInt("Customer_ID"),
                    filterResults.getInt("User_ID"));
            filterByMonthList.add(appointments);
        }
        return filterByMonthList;
    }

	/**
	 * Getter for Highlighted Appointment
	 */
    public static Appointments getHighlightedAppointment() {
        return highlightedAppointment;
    }

	/**
	 * Goes to Main Menu
     * @param actionEvent Handles the event when the button is called
	 */
    public void onActionMainMenu(ActionEvent actionEvent) throws IOException {
        buttonChanging(actionEvent, "/view/mainMenu.fxml");
    }

    /**
	 * Changes Screen to New Screen
     * @param actionEvent The action event
     * @param resourcesString The link to the new screen
	 */
    public void buttonChanging(ActionEvent actionEvent, String resourcesString) throws IOException {
        stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource(resourcesString));
        stage.setScene(new Scene(scene));
        stage.show();
    }

	/**
	 * Filters Appointments by Current Week
     * @param actionEvent Radio button is selected
	 */
    public void onActionFilterByWeek(ActionEvent actionEvent) throws SQLException {
        filterByMonthList.clear();
        filterByWeek().clear();
        appointmentTblView.setItems(filterByWeek());
    }

    /**
	 * Deletes the Appointments From the DB
     * @param actionEvent Handles the event when the button is called
	 */
    public void onActionDeleteAppointment(ActionEvent actionEvent) throws SQLException, IOException {

        Appointments highlightedAppointment = appointmentTblView.getSelectionModel().getSelectedItem();

        if(highlightedAppointment == null) {
            Alerts.alertDisplays(9);
        } else {
            Alert alertForDelete = new Alert(Alert.AlertType.CONFIRMATION);
            alertForDelete.setHeaderText("Sure you want to delete this appointment?");
            alertForDelete.setContentText("Select OK to delete the appointment");
            Optional<ButtonType> deleteResult = alertForDelete.showAndWait();

            if(deleteResult.isPresent() && deleteResult.get() == ButtonType.OK) {

                DataBaseQueries.deleteFromAppointmentsTable(highlightedAppointment.getAppointmentId());

                Alerts.informationAlert("Appointment Cancelled",
                        "Appointment ID was " +  highlightedAppointment.getAppointmentId(),
                        "It was a " + highlightedAppointment.getType() + " meeting with " + highlightedAppointment.getContactName());

                buttonChanging(actionEvent, "/view/appointmentScreen.fxml");
            }
        }
    }

	/**
	 * Goes to Add Appointment Screen
     * @param actionEvent Handles the event when the button is called
	 */
    public void onActionAddAppointment(ActionEvent actionEvent) throws IOException {
        buttonChanging(actionEvent, "/view/addAppointment.fxml");
    }

    /**
	 * Goes to Appointment Screen
     * @param actionEvent The action event
	 */
    public void onActionAppointments(ActionEvent actionEvent) throws IOException {
        buttonChanging(actionEvent, "/view/appointmentScreen.fxml");
    }
    public void onActionDefaultTable(ActionEvent actionEvent) throws IOException {
        onActionAppointments(actionEvent);
    }

    /**
	 * Gets monthID
	 */
    public int monthSelectionToID(String selectedMonth) {
        int monthId;
        switch(selectedMonth){
            case "FEBRUARY":
                monthId = 2;
                break;
            case "MARCH":
                monthId = 3;
                break;
            case "APRIL":
                monthId = 4;
                break;
            case "MAY":
                monthId = 5;
                break;
            case "JUNE":
                monthId = 6;
                break;
            case "JULY":
                monthId = 7;
                break;
            case "AUGUST":
                monthId = 8;
                break;
            case "SEPTEMBER":
                monthId = 9;
                break;
            case "OCTOBER":
                monthId = 10;
                break;
            case "NOVEMBER":
                monthId = 11;
                break;
            case "DECEMBER":
                monthId = 12;
                break;
            default:
                monthId = 1;
        }
        return monthId;
    }
}