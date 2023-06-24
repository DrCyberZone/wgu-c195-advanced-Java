package controller;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import model.Alerts;
import model.Appointments;
import util.JDBC;

/**
 * Method and Class to Display All Reports
 * @author Seyed Hassanzadeh
 */

public class AllReports implements Initializable {

    /**
	 * Type Column
	 */
    public TableColumn<Appointments, String> typeTblCol;
    /**
	 * Location Column
	 */
    public TableColumn<Appointments, String> locationTblCol;
	/**
	 * Number of Appointments Per Customer Text Field
	 */
    public TextField numberOfAppsTextFld;
    /**
	 * Schedule Table Field
	 */
    public TableView<Appointments> scheduleOfEachContactTblView;
    /**
	 * Save Button
	 */
    public ComboBox<String> customerComboBox;
	/**
	 * Wait on This
	 */
    public Label filterReportsBtn;
    /**
	 * Main Menu Button
	 */
    public Button mainMenuBtn;
    /**
	 * Month Combo Box
	 */
    public ComboBox<String> monthCombo;
    /**
	 * Type Combo Box
	 */
    public ComboBox<String> typeCombo;
    /**
	 * Description Column
	 */
    public TableColumn<Appointments, String> descriptionTblCol;
    /**
	 * Total Number of Appointments by Month/Type
	 */
    public Label totalNumberByMonthAndType;
    /**
	 * Total Number of Appointment for Selected Customer
	 */
    public Label totalAppointmentsByCustomer;
    /**
	 * Save Button
	 */
    public Button appointmentCountSearchBtn;
    /**
	 * Save Button
	 */
    public ComboBox<String> contactCombo;
    /**
	 * Save Button
	 */
    public int contactId;
    /**
	 * AppointmentID Column
	 */
    public TableColumn<Appointments, Integer> appointmentIdTblCol;
    /**
	 * Title Column
	 */
    public TableColumn<Appointments, String> titleTblCol;
    /**
	 * UserID Column
	 */
    public TableColumn<Appointments, Integer> userIdTblCol;
    /**
	 * CustomerID Column
	 */
    public TableColumn<Appointments, Integer> customerIdTblCol;
    /**
	 * Ending Time Column
	 */
    public TableColumn<Appointments, LocalDateTime> endTblCol;
    /**
	 * Starting Time Column
	 */
    public TableColumn<Appointments, LocalDateTime> startTblCol;
    /**
	 * Contact Name Column
	 */
    public TableColumn<Appointments, String> contactTblCol;
    /**
	 * Contact Name Observable List
	 */
    private ObservableList<Appointments> contactAppointmentSchedule = FXCollections.observableArrayList();
	/**
	 * Months Observable List
	 */
    private ObservableList<String> monthList = FXCollections.observableArrayList("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY",
            "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER");
	/**
	 * All Customers Observable List
	 */
    private ObservableList<String> customerList = FXCollections.observableArrayList();
	/**
	 * Meetings Observable List
	 */
    private ObservableList<String> typeList = FXCollections.observableArrayList("Meet and Greet", "Conference", "Planning Session");
    /**
	 * All Contacts Observable List
	 */
    private ObservableList<String> contactList = FXCollections.observableArrayList();
	/**
	 * Counts All Selected Customers Appointments
     * @param actionEvent Combo box selection
	 */
    public void onActionCustomerComboBox(ActionEvent actionEvent) throws SQLException {

        String customerName = customerComboBox.getSelectionModel().getSelectedItem();
        Statement getCustomerCount = JDBC.getConnection().createStatement();
        String customerCountSQL = "SELECT COUNT(Customer_Name) AS 'Total' FROM appointments " +
                                "INNER JOIN customers " +
                                "ON appointments.Customer_ID = customers.Customer_ID " +
                                "WHERE Customer_Name='" + customerName + "'";
        ResultSet rs = getCustomerCount.executeQuery(customerCountSQL);
        while(rs.next()) {
            totalAppointmentsByCustomer.setText(rs.getString("Total"));
        }
    }

	/**
	 * Queries DB for All Appointments by Month/Type.
     * @param type Type of appointment
     * @param monthId ID of the month selected
	 */
    public void searchAppointmentsByMonthAndType(int monthId, String type) throws SQLException {

        Statement appointmentStatement = JDBC.getConnection().createStatement();
        String searchByMonthAndType = "SELECT COUNT(Appointment_ID) AS Count FROM appointments WHERE month(Start)=" + monthId + " AND Type='" + type + "'";
        ResultSet appointmentCount = appointmentStatement.executeQuery(searchByMonthAndType);

        while(appointmentCount.next()) {
            totalNumberByMonthAndType.setText(String.valueOf(appointmentCount.getInt("Count")));
        }
    }

    /**
	 * Stage Variable
	 */
    Stage stage;
	/**
	 * Scene Variable
	 */
    Parent scene;

	/**
	 * Populates Contact Schedule Table
     * @param actionEvent Handles combo box selection
	 */
    public void onActionContactAppointmentTable(ActionEvent actionEvent) throws SQLException {
        getContactsSchedule();
        try {
            getContactsSchedule().clear();
            scheduleOfEachContactTblView.setItems(getContactsSchedule());
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        appointmentIdTblCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        titleTblCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionTblCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationTblCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactTblCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        typeTblCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        startTblCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endTblCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerIdTblCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        userIdTblCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
    }

	/**
	 * Sets Contacts list
	 */
    public void loadContactList() throws SQLException {
        Statement loadContactStatement = JDBC.getConnection().createStatement();
        String loadContactNameSQL = "SELECT * FROM contacts";
        ResultSet loadContactResults = loadContactStatement.executeQuery(loadContactNameSQL);

        while(loadContactResults.next()) {
            contactList.add(loadContactResults.getString("Contact_Name"));
            contactCombo.setItems(contactList);
        }
    }

	/**
	 *  Sets Customers list
	 */
    public void loadCustomersList() throws SQLException {

        Statement loadCustomersStatement = JDBC.getConnection().createStatement();
        String loadCustomerNameSQL = "SELECT * FROM customers";
        ResultSet loadCustomerResults = loadCustomersStatement.executeQuery(loadCustomerNameSQL);

        while(loadCustomerResults.next()) {
            customerList.add(loadCustomerResults.getString("Customer_Name"));
            customerComboBox.setItems(customerList);
        }
    }

	/**
	 * Populates Contact Appointments Schedule list
     * @return List of selected contact appointments
	 */
    public ObservableList<Appointments> getContactsSchedule() throws SQLException {
        String contactName = contactCombo.getSelectionModel().getSelectedItem();

        Statement statement = JDBC.getConnection().createStatement();
        String appointmentInfoSQL = "SELECT appointments.*, contacts.* " +
                                "FROM appointments " +
                                "INNER JOIN contacts " +
                                "ON appointments.Contact_ID = contacts.Contact_ID " +
                                "WHERE Contact_Name='" + contactName + "'";

        ResultSet appointmentResults = statement.executeQuery(appointmentInfoSQL);

        while(appointmentResults.next()) {
            Appointments appointments = new Appointments(appointmentResults.getInt("Appointment_ID"),
                    appointmentResults.getString("Title"),
                    appointmentResults.getString("Description"),
                    appointmentResults.getString("Location"),
                    appointmentResults.getString("Contact_Name"),
                    appointmentResults.getString("Type"),
                    appointmentResults.getTimestamp("Start").toLocalDateTime(),
                    appointmentResults.getTimestamp("End").toLocalDateTime(),
                    appointmentResults.getInt("Customer_ID"),
                    appointmentResults.getInt("User_ID"));
            contactAppointmentSchedule.add(appointments);
        }
        return contactAppointmentSchedule;
    }

	/**
	 * Search Button to Search by Month/Type.
     * @param actionEvent Handles when the button is called
	 */
    public void onActionSearchNumberOfAppointments(ActionEvent actionEvent) throws SQLException {

        if(monthCombo.getSelectionModel().getSelectedItem() == null) {
            Alerts.errorAlert("Month Empty", "Please fill in the Month combo box.", "");
        } else if(typeCombo.getSelectionModel().getSelectedItem() == null) {
            Alerts.errorAlert("Type Empty", "Please fill in the Type combo box.", "");
        } else {
            String selectedMonth = monthCombo.getSelectionModel().getSelectedItem();
            int monthId = monthSelectionToID(selectedMonth);
            String type = typeCombo.getSelectionModel().getSelectedItem();
            searchAppointmentsByMonthAndType(monthId, type);
        }
    }

	/**
	 * Initializes Combo Boxes
	 */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadCustomersList();
            loadContactList();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        monthCombo.setItems(monthList);
        typeCombo.setItems(typeList);
    }

	/**
	 * Changed Screen to New Screen
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
	 * Takes Users to Main Menu
     * @param actionEvent Main menu button
	 */
    public void onActionMainMenu(ActionEvent actionEvent) throws IOException {
        buttonChanging(actionEvent, "/view/mainMenu.fxml");
    }

	/**
	 * Gets the monthID
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