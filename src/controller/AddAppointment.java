package controller;

import model.Alerts;
import util.DataBaseQueries;
import util.JDBC;
import util.TimeManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Method and Class for Adding Appointments
 * @author Seyed Hassanzadeh
 */

public class AddAppointment implements Initializable {
    /**
	 * Location Text Field
	 */
    public TextField locationTextFld;
    /**
	 * CustomerID Text Field
	 */
    public TextField customerIdTextFld;
    /**
	 * Title Text Field
	 */
    public TextField titleTxtFld;
    /**
	 * End Time Combo Box
	 */
    public ComboBox<LocalTime> endTimeComboBox;
    /**
	 * Start Time Combo Box
	 */
    public ComboBox<LocalTime> startTimeComboBox;
    /**
	 * Date Picker
	 */
    public DatePicker dateDatePicker;
    /**
	 * Save Appointment
	 */
    public Button saveAppointmentBtn;
    /**
	 * Existing Customer Combo Box
	 */
    public ComboBox<String> existingCustomerComboBox;
    /**
	 * Description Text Field
	 */
    public TextField descriptionTxtFld;
    /**
	 * Contact Name Combo Box
	 */
    public ComboBox<String> contactComboBox;
	/**
	 * Back Button
	 */
    public Button backBtn;
    /**
	 * Main Menu Button
	 */
    public Button mainMenuBtn;
    /**
	 * Contract Text Field
	 */
    public TextField contractTxtFld;
    /**
	 * UserID
	 */
    public ComboBox<Integer> userIdCombo;
    /**
	 * Type Combo Box
	 */
    public ComboBox<String> typeComboBox;
    /**
	 * ContactId Variable
	 */
    private int contactId;
    /**
	 * Getter For ContactID
	 */
    public int getContactId() {return contactId;}
    /**
	 * Setter For ContactID
	 */
    public void setContactId(int contactId) {
        this.contactId = contactId;
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
	 * Returns to Appointments Screen
     * @param actionEvent Handles the event when button is called
	 */
    public void onActionBack(ActionEvent actionEvent) throws IOException {
        Alert alertForBack = new Alert(Alert.AlertType.CONFIRMATION);
        alertForBack.setTitle("Cancel");
        alertForBack.setHeaderText("Sure You Want To Go Back?");
        alertForBack.setContentText("This will clear all text fields and your data will be lost");
        Optional<ButtonType> backSelection = alertForBack.showAndWait();

        if(backSelection.isPresent() && backSelection.get() == ButtonType.OK) {
            buttonChanging(actionEvent, "/view/appointmentScreen.fxml");
        }
    }
	/**
	 * Changes the Screen
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
	 * Validates the Appointment Time
     * @param end Timestamp of end time
     * @param start Timestamp of start time
	 */
    public boolean isValidAppointment(Timestamp start, Timestamp end) {

        boolean endBeforeStart = end.before(start);
        boolean endEqualsStart = end.equals(start);

        if(endBeforeStart) {
            Alerts.alertDisplays(24);
            return false;
        } else if(endEqualsStart) {
            Alerts.alertDisplays(25);
            return false;
        }

        ZonedDateTime startZDT = ZonedDateTime.of(start.toLocalDateTime(), ZoneId.systemDefault());
        ZonedDateTime endZDT = ZonedDateTime.of(end.toLocalDateTime(), ZoneId.systemDefault());
        ZonedDateTime utcStartZDT = ZonedDateTime.ofInstant(startZDT.toInstant(), ZoneId.of("UTC"));
        ZonedDateTime utcEndZDT = ZonedDateTime.ofInstant(endZDT.toInstant(), ZoneId.of("UTC"));
        Timestamp startUTC = Timestamp.valueOf(utcStartZDT.toLocalDateTime());
        Timestamp endUTC = Timestamp.valueOf(utcEndZDT.toLocalDateTime());

        try {
            Statement validAppointmentStatement = JDBC.getConnection().createStatement();

            String validAptSQL =
                    "SELECT * " +
                    "FROM client_schedule.appointments " +
                    "WHERE ('" + startZDT + "' BETWEEN Start AND End " +
                    "OR '" + endZDT + "' BETWEEN Start AND End)";

            ResultSet checkAptValidation = validAppointmentStatement.executeQuery(validAptSQL);

            if(checkAptValidation.next()) {
                Alerts.alertDisplays(26);
                return false;
            }
        } catch (SQLException se) {
            se.getMessage();
        }
        return true;
    }
    /**
	 * Users Customers Observable List
	 */
    public static ObservableList<Integer> userIdList = FXCollections.observableArrayList();
    /**
	 * Types Customers Observable List
	 */
    private ObservableList<String> typeList = FXCollections.observableArrayList("Diversity Day", "Conference", "Sales Meeting");
	/**
	 * Existing Customers Observable List
	 */
    public static ObservableList<String> existingCutList = FXCollections.observableArrayList();
    /**
	 * Contact Customers Observable List
	 */
    public static ObservableList<String> contactNameList = FXCollections.observableArrayList();

    /**
	 * Combo Boxes
     * @param url URL
     * @param resourceBundle Resource Bundle
	 */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        existingCutList.clear();
        contactNameList.clear();
        userIdList.clear();
        TimeManager startTime = new TimeManager();
        startTimeComboBox.setItems(startTime.generateTimeList());
        startTimeComboBox.getSelectionModel().selectFirst();
        TimeManager endTime = new TimeManager();
        ObservableList<LocalTime> endTimeList = endTime.generateTimeList();
        endTimeList.add(LocalTime.of(0, 0));
        endTimeComboBox.setItems(endTimeList);
        endTimeComboBox.getSelectionModel().selectFirst();
        typeComboBox.setItems(typeList);

        try {
            Statement populateExistingCustomers = JDBC.getConnection().createStatement();
            String sqlStatement = "SELECT * FROM customers";
            ResultSet result = populateExistingCustomers.executeQuery(sqlStatement);

            while(result.next()) {
                existingCutList.add(result.getString("Customer_Name"));
                existingCustomerComboBox.setItems(existingCutList);
            }
            populateExistingCustomers.close();

            Statement populateContactStatement = JDBC.getConnection().createStatement();
            String sqlContactStatement = "SELECT * FROM contacts";
            ResultSet contactResult = populateContactStatement.executeQuery(sqlContactStatement);

            while(contactResult.next()) {
                contactNameList.add(contactResult.getString("Contact_Name"));
                contactComboBox.setItems(contactNameList);
            }
            populateContactStatement.close();

            userIdList.clear();

            Statement populateUserIdStatement = JDBC.getConnection().createStatement();
            String sqlUserIdStatement = "SELECT * FROM users";
            ResultSet userIdResult = populateUserIdStatement.executeQuery(sqlUserIdStatement);

            while(userIdResult.next()) {

                userIdList.add(userIdResult.getInt("User_ID"));
                userIdCombo.setItems(userIdList);
            }
            populateUserIdStatement.close();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
    /**
	 * Saves Customers on DB
     * @param actionEvent Handles the event when the button is called
	 */
    public void onActionSaveAppointment(ActionEvent actionEvent) throws IOException, SQLException {

        try {
            String titleInfo = titleTxtFld.getText();
            String descInfo = descriptionTxtFld.getText();
            String locationInfo = locationTextFld.getText();
            int contactInfo = contactId;
            String typeInfo = typeComboBox.getSelectionModel().getSelectedItem();
            int costID = Integer.parseInt(customerIdTextFld.getText());
            int userID = userIdCombo.getSelectionModel().getSelectedItem();
            LocalDate date = dateDatePicker.getValue();
            LocalTime start = startTimeComboBox.getSelectionModel().getSelectedItem();
            LocalTime end = endTimeComboBox.getSelectionModel().getSelectedItem();
            Timestamp startTimestamp = Timestamp.valueOf(LocalDateTime.of(date, start));
            Timestamp endTimestamp = Timestamp.valueOf(LocalDateTime.of(date, end));

            boolean outsideBusinessHours = TimeManager.isOutsideBusinessHours(date, start, end, ZoneId.systemDefault());

            if (titleNotNull(titleInfo) && descriptionNotNull(descInfo) && typeNotNull(typeInfo) && locationNotNull(locationInfo) && startNotNull(startTimestamp) &&
                    endNotNull(endTimestamp) && dateNotNull(date) && customerNotNull(costID) &&
                    contactNotNull(contactId) && userIdNotNull(userID) && isValidAppointment(startTimestamp, endTimestamp) && !outsideBusinessHours) {

                DataBaseQueries.insertAppointment(titleInfo, descInfo, locationInfo, typeInfo, startTimestamp, endTimestamp, costID, userID, contactInfo);
                Alerts.alertDisplays(23);
                buttonChanging(actionEvent, "/view/appointmentScreen.fxml");
            }

        } catch (Exception e) {
            if (customerIdTextFld.getText() == null) {
                Alerts.alertDisplays(20);
            } else if (userIdCombo.getSelectionModel().getSelectedItem() == null) {
                Alerts.alertDisplays(21);
            } else if (dateDatePicker.getValue() == null) {
                Alerts.alertDisplays(17);
            } else if (startTimeComboBox.getSelectionModel().getSelectedItem() == null) {
                Alerts.alertDisplays(18);
            } else if (endTimeComboBox.getValue() == null) {
                Alerts.alertDisplays(19);
            } else if (existingCustomerComboBox.getValue() == null) {
                Alerts.alertDisplays(20);
            }
        }
    }
    /**
	 * Returns to Main Menu
     * @param actionEvent Handles the event when the button is called
	 */
    public void onActionMainMenu(ActionEvent actionEvent) throws IOException {
        Alert alertForMainMenu = new Alert(Alert.AlertType.CONFIRMATION);
        alertForMainMenu.setTitle("Cancel");
        alertForMainMenu.setHeaderText("Sure You Want To Go the Main Menu?");
        alertForMainMenu.setContentText("This will clear all text fields and your data will be lost");
        Optional<ButtonType> MainMenuSelection = alertForMainMenu.showAndWait();

        if(MainMenuSelection.isPresent() && MainMenuSelection.get() == ButtonType.OK) {
            buttonChanging(actionEvent, "/view/mainMenu.fxml");
        }
    }
    /**
	 * Sets the CustomerID
     * @param actionEvent Handles the event when the button is called
	 */
    public void onActionExistingCustomer(ActionEvent actionEvent) throws SQLException {
        String customerName = existingCustomerComboBox.getSelectionModel().getSelectedItem();
        Statement st = JDBC.getConnection().createStatement();
        String sql = "SELECT Customer_ID FROM customers WHERE Customer_Name='" + customerName + "'";
        ResultSet resultSet = st.executeQuery(sql);

        while(resultSet.next()){
            customerIdTextFld.setText(String.valueOf(resultSet.getInt("Customer_ID")));
        }
        st.close();
    }
    /**
	 * Displays Error When The Contact Combo Box is Empty
     * @param contact The text in the contact combo box
	 */
    public boolean contactNotNull(int contact) {
        if (contactComboBox.getSelectionModel().getSelectedItem() == null) {
            Alerts.alertDisplays(22);
            return false;
        }
        return true;
    }
	/**
	 * Displays Error When The Description Field is Empty
     * @param desc The text in the description
	 */
    public boolean descriptionNotNull(String desc) {
        if (descriptionTxtFld.getText().isEmpty()) {
            Alerts.alertDisplays(14);
            return false;
        }
        return true;
    }
	/**
	 * Displays Error When The Start Combo Box is Empty
     * @param start The timestamp of the start combo box
	 */
    public boolean startNotNull(Timestamp start) {
        if (startTimeComboBox.getSelectionModel().getSelectedItem() == null) {
            Alerts.alertDisplays(18);
            return false;
        }
        return true;
    }
	/**
	 * Displays Error When The Location Field is Empty
     * @param location The text in the location
	 */
    public boolean locationNotNull(String location) {
        if (locationTextFld.getText().isEmpty()) {
            Alerts.alertDisplays(15);
            return false;
        }
        return true;
    }
	/**
	 * Displays Error When The Date Picker is Empty
     * @param date The local date of the date picker
	 */
    public boolean dateNotNull(LocalDate date) {
        if (dateDatePicker.getValue() == null) {
            Alerts.alertDisplays(17);
            return false;
        }
        return true;
    }
	/**
	 * Displays Error When The Title Field is Empty
     * @param title The text in the title
	 */
    public boolean titleNotNull(String title) {
        if (titleTxtFld.getText().isEmpty()) {
            Alerts.alertDisplays(13);
            return false;
        }
        return true;
    }
	/**
	 * Displays Error When The End Combo Box is Empty
     * @param end The timestamp of the end combo box
	 */
    public boolean endNotNull(Timestamp end) {
        if (endTimeComboBox.getSelectionModel().getSelectedItem() == null) {
            Alerts.alertDisplays(19);
            return false;
        }
        return true;
    }
	/**
	 * Displays Error When The UserId Field is Empty
     * @param userId The text in the userId
	 */
    public boolean userIdNotNull(int userId) {
        if (userIdCombo.getSelectionModel().getSelectedItem() == null) {
            Alerts.alertDisplays(21);
            return false;
        }
        return true;
    }
	/**
	 * Displays Error When The Type Combo Box is Empty
     * @param type The text in the type
	 */
    public boolean typeNotNull(String type) {
        if (typeComboBox.getSelectionModel().getSelectedItem() == null) {
            Alerts.alertDisplays(16);
            return false;
        }
        return true;
    }
	/**
	 * Displays Error When The CustomerId Field is Empty
     * @param customerId The text in the customerId
	 */
    public boolean customerNotNull(int customerId) {
        if (existingCustomerComboBox.getSelectionModel().getSelectedItem() == null) {
            Alerts.alertDisplays(20);
            return false;
        }
        return true;
    }
    /**
	 * Sets the contact id text field
     * @param actionEvent Handles the event when the button is called
	 */
    public void onActionContactComboBox(ActionEvent actionEvent) throws SQLException {
        String contactName = contactComboBox.getSelectionModel().getSelectedItem();

        Statement st = JDBC.getConnection().createStatement();
        String sql = "SELECT Contact_ID FROM contacts WHERE Contact_Name='" + contactName + "'";
        ResultSet resultSet = st.executeQuery(sql);

        while(resultSet.next()){
            int contactId = resultSet.getInt("Contact_ID");
            setContactId(contactId);
        }
        st.close();
    }
}