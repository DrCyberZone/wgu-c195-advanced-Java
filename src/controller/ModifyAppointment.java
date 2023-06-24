package controller;

import model.Alerts;
import model.Appointments;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.util.Optional;
import java.util.ResourceBundle;
import util.DataBaseQueries;
import util.JDBC;
import util.TimeManager;

/**
 * Modify Appointment Controller Class
 * @author Seyed Hassanzadeh
 */

public class ModifyAppointment implements Initializable {

    /**
	 * AppointmentID Text Field
	 */
    public TextField appointmentTxtFld;
    /**
	 * Location Text Field
	 */
    public TextField locationTextFld;
    /**
	 * Description Text Field
	 */
    public TextField descriptionTextFld;
	/**
	 * Contact Name Combo Box
	 */
    public ComboBox<String> contactNameCombo;
    /**
	 * CustomerID Text Field
	 */
    public TextField customerIdTextFld;
    /**
	 * Type Combo Box
	 */
    public ComboBox<String> typeComboBox;
	/**
	 * Save Button
	 */
    public Button saveAppointmentBtn;
	/**
	 * Back Button
	 */
    public Button backBtn;
    /**
	 * Customer Name Combo Box
	 */
    public ComboBox<String> customerComboBox;
    /**
	 * UserID Combo Box
	 */
    public ComboBox<Integer> userIdCombo;
    /**
	 * Title Text Field
	 */
    public TextField titleTextFld;
	/**
	 * Date Picker
	 */
    public DatePicker dateDatePicker;
	/**
	 * Main Menu Button
	 */
    public Button mainMenuBtn;
    /**
	 * End Time Combo Box
	 */
    public ComboBox<LocalTime> endTimeComboBox;
    /**
	 * Start Time Combo Box
	 */
    public ComboBox<LocalTime> startTimeComboBox;

    Parent scene;
    Stage stage;

    /**
	 * Highlighted Appointment
	 */
    private static Appointments highlightedAppointment;
    /**
	 * ContactID
	 */
    private int contactId;
    /**
	 * UserID Observable List
	 */
    public static ObservableList<Integer> userIdList = FXCollections.observableArrayList();
    /**
	 * Types of Observable List
	 */
    private ObservableList<String> typeList = FXCollections.observableArrayList("Meet and Greet", "Conference", "Planning Session");
	/**
	 * Existing Customers Observable List
	 */
    public static ObservableList<String> existingCutList = FXCollections.observableArrayList();
    /**
	 * Contacts Name Observable List
	 */
    public static ObservableList<String> contactNameList = FXCollections.observableArrayList();
    /**
	 * Setter for ContactID
	 */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }
	/**
	 * Saves Appointments to Database
     * @param actionEvent The action event.
	 */
    public void onActionSaveAppointment(ActionEvent actionEvent) throws IOException {

        try {
            int custID = Integer.parseInt(customerIdTextFld.getText());
            int userID = userIdCombo.getSelectionModel().getSelectedItem();
            int appointmentId = highlightedAppointment.getAppointmentId();
            String titleInfo = titleTextFld.getText();
            String descInfo = descriptionTextFld.getText();
            String locationInfo = locationTextFld.getText();
            String typeInfo = typeComboBox.getSelectionModel().getSelectedItem();
            LocalDate date = dateDatePicker.getValue();
            LocalTime start = startTimeComboBox.getSelectionModel().getSelectedItem();
            LocalTime end = endTimeComboBox.getSelectionModel().getSelectedItem();
            Timestamp startTimestamp = Timestamp.valueOf(LocalDateTime.of(date, start));
            Timestamp endTimestamp = Timestamp.valueOf(LocalDateTime.of(date, end));

            boolean outsideBusinessHours = TimeManager.isOutsideBusinessHours(date, start, end, ZoneId.systemDefault());

            if(titleNotNull(titleInfo) && descriptionNotNull(descInfo) && typeNotNull(typeInfo) && locationNotNull(locationInfo) && startNotNull(startTimestamp) &&
                    endNotNull(endTimestamp) && dateNotNull(date) && customerNotNull(custID) &&
                    contactNotNull(contactId) && userIdNotNull(userID) && isValidAppointment(startTimestamp, endTimestamp) && !outsideBusinessHours) {

                Alerts.alertDisplays(23);
                DataBaseQueries.updateAppointment(appointmentId, titleInfo, descInfo, locationInfo, typeInfo,
                                                    startTimestamp, endTimestamp, custID, userID, contactId);

                buttonChanging(actionEvent, "/view/appointmentScreen.fxml");
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
            if(customerIdTextFld.getText() == null) {
                Alerts.alertDisplays(20);
            } else if(userIdCombo.getSelectionModel().getSelectedItem() == null) {
                Alerts.alertDisplays(21);
            } else if(dateDatePicker.getValue() == null) {
                Alerts.alertDisplays(17);
            } else if(startTimeComboBox.getSelectionModel().getSelectedItem() == null) {
                Alerts.alertDisplays(18);
            } else if(endTimeComboBox.getValue() == null) {
                Alerts.alertDisplays(19);
            } else if(customerComboBox.getValue() == null) {
                Alerts.alertDisplays(20);
            }
        }
    }

    /**
	 * Goes to The Main Menu
     * @param actionEvent The action event.
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
	 * Goes to Appointments Screen
     * @param actionEvent The action event
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
	 * Calls to Get The ContactID
     * @param actionEvent Action even
	 */
    public void onActionContactComboBoc(ActionEvent actionEvent) throws SQLException {
        getContactIDFromContactName();
    }

    /**
	 * Loads the Data into the Matching Fields
	 */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        highlightedAppointment = AppointmentScreen.getHighlightedAppointment();

        appointmentTxtFld.setText(String.valueOf(highlightedAppointment.getAppointmentId()));
        titleTextFld.setText(highlightedAppointment.getTitle());
        locationTextFld.setText(highlightedAppointment.getLocation());
        descriptionTextFld.setText(highlightedAppointment.getDescription());
        typeComboBox.setValue(highlightedAppointment.getType());
        contactNameCombo.setValue(highlightedAppointment.getContactName());

        dateDatePicker.setValue(highlightedAppointment.getStart().toLocalDate());
        startTimeComboBox.setValue(highlightedAppointment.getStart().toLocalTime());
        endTimeComboBox.setValue((highlightedAppointment.getEnd().toLocalTime()));
        customerIdTextFld.setText(String.valueOf(highlightedAppointment.getCustomerId()));
        userIdCombo.setValue(highlightedAppointment.getUserId());
        try {
            getContactIDFromContactName();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        TimeManager startTime = new TimeManager();
        startTimeComboBox.setItems(startTime.generateTimeList());

        TimeManager endTime = new TimeManager();
        ObservableList<LocalTime> endTimeList = endTime.generateTimeList();
        endTimeList.add(LocalTime.of(0, 0));

        endTimeComboBox.setItems(endTimeList);
        typeComboBox.setItems(typeList);

        existingCutList.clear();
        contactNameList.clear();
        userIdList.clear();

        try {
            Statement st = JDBC.getConnection().createStatement();
            String sql = "SELECT * FROM customers WHERE Customer_ID=" + highlightedAppointment.getCustomerId();
            ResultSet rs = st.executeQuery(sql);

            if(rs.next()) {
                customerComboBox.setValue(rs.getString("Customer_Name"));
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        try {
            Statement populateExistingCustomers = JDBC.getConnection().createStatement();
            String sqlStatement = "SELECT * FROM customers";
            ResultSet result = populateExistingCustomers.executeQuery(sqlStatement);

            while(result.next()) {
                existingCutList.add(result.getString("Customer_Name"));
                customerComboBox.setItems(existingCutList);
            }
            populateExistingCustomers.close();

            Statement populateContactStatement = JDBC.getConnection().createStatement();
            String sqlContactStatement = "SELECT * FROM contacts";
            ResultSet contactResult = populateContactStatement.executeQuery(sqlContactStatement);

            while(contactResult.next()) {
                contactNameList.add(contactResult.getString("Contact_Name"));
                contactNameCombo.setItems(contactNameList);
            }
            populateContactStatement.close();

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
	 * Gets ContactID From the Selected Contact Name
	 */
    public void getContactIDFromContactName() throws SQLException {
        String contactName = contactNameCombo.getSelectionModel().getSelectedItem();

        Statement st = JDBC.getConnection().createStatement();
        String sql = "SELECT Contact_ID FROM contacts WHERE Contact_Name='" + contactName + "'";
        ResultSet resultSet = st.executeQuery(sql);

        while(resultSet.next()){
            int contactId = resultSet.getInt("Contact_ID");
            setContactId(contactId);
        }
        st.close();
    }
	/**
	 * Populates AppointmentID With Matching Customer Information
     * @param actionEvent The action event
	 */
    public void onActionCustomerCombo(ActionEvent actionEvent) throws SQLException {
        String customerName = customerComboBox.getSelectionModel().getSelectedItem();

        Statement st = JDBC.getConnection().createStatement();
        String sql = "SELECT Customer_ID FROM customers WHERE Customer_Name='" + customerName + "'";
        ResultSet resultSet = st.executeQuery(sql);

        while(resultSet.next()){
            customerIdTextFld.setText(String.valueOf(resultSet.getInt("Customer_ID")));
        }
        st.close();
    }
    /**
	 * Checks Appointments Inputs
     * @param start Start appointment timestamp
     * @param end End appointment timestamp
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

            String validAppsSQL =
                    "SELECT * " +
                    "FROM client_schedule.appointments " +
                    "WHERE ('" + startZDT + "' BETWEEN Start AND End " +
                    "OR '" + endZDT + "' BETWEEN Start AND End) " +
                    "AND Appointment_ID !=" + appointmentTxtFld.getText();

            ResultSet checkAppsValidation = validAppointmentStatement.executeQuery(validAppsSQL);

            if(checkAppsValidation.next()) {
                Alerts.alertDisplays(26);
                return false;
            }
        } catch (SQLException se) {
            se.getMessage();
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
	 * Displays Error When The Description Field is Empty
     * @param desc The text in the description
	 */
    public boolean descriptionNotNull(String desc) {
        if (descriptionTextFld.getText().isEmpty()) {
            Alerts.alertDisplays(14);
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
	 * Displays Error When The Type Field is Empty
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
	 * Displays Error When The Contact Field is Empty
     * @param contact The text in the location
	 */
    public boolean contactNotNull(int contact) {
        if (contactNameCombo.getSelectionModel().getSelectedItem() == null) {
            Alerts.alertDisplays(22);
            return false;
        }
        return true;
    }
    /**
	 * Displays Error When The Start Combo Box is Empty
     * @param start The time of the start combo box
	 */
    public boolean startNotNull(Timestamp start) {
        if (startTimeComboBox.getSelectionModel().getSelectedItem() == null) {
            Alerts.alertDisplays(18);
            return false;
        }
        return true;
    }
    /**
	 * Displays Error When The End Combo Box is Empty
     * @param end The time of the end combo box
	 */
    public boolean endNotNull(Timestamp end) {
        if (endTimeComboBox.getSelectionModel().getSelectedItem() == null) {
            Alerts.alertDisplays(19);
            return false;
        }
        return true;
    }
    /**
	 * Displays Error When The CustomerId Field is Empty
     * @param customerId The text in the customerId
	 */
    public boolean customerNotNull(int customerId) {
        if (customerComboBox.getSelectionModel().getSelectedItem() == null) {
            Alerts.alertDisplays(20);
            return false;
        }
        return true;
    }
    /**
	 * Displays Error When The Title Field is Empty
     * @param title The text in the title
	 */
    public boolean titleNotNull(String title) {
        if (titleTextFld.getText().isEmpty()) {
            Alerts.alertDisplays(13);
            return false;
        }
        return true;
    }
}