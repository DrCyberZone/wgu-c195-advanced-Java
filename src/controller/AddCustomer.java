package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import model.Alerts;
import model.Customer;
import util.DataBaseQueries;
import util.DataProvider;
import util.JDBC;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Method and Class to Control Customer Data
 * @author Seyed Hassanzadeh
 */

public class AddCustomer implements Initializable {

    /**
	 * Customer City Combo Box
	 */
    public ComboBox<String> cityComboBox;
    /**
	 * CustomerID Text Field
	 */
    public TextField customerIdTextFld;
    /**
	 * Customer Country Combo Box
	 */
    public ComboBox<String> countryComboBox;
    /**
	 * DivisionID
	 */
    public int divisionIDFromCity;
	/**
	 * Save Customer Button
	 */
    public Button saveCustomerBtn;
    /**
	 * Cancel Customer Button
	 */
    public Button cancelBtn;
    /**
	 * Customer Name Text Field
	 */
    public TextField nameTxtFld;
    /**
	 * Customer Address Text Field
	 */
    public TextField addressTxtFld;
    /**
	 * Customer Postal Code Text Field
	 */
    public TextField postalCodeTxtFld;
    /**
	 * Customer Phone Number Text Field
	 */
    public TextField phoneTxtFld;
    
    /**
	 * Sets the Cities Combo Box
     * @param actionEvent Handles the event when button is called
	 */
    public void onActionCountryComboBox(ActionEvent actionEvent) throws SQLException {
        String countrySelected = countryComboBox.getSelectionModel().getSelectedItem();

        if(countrySelected.equals("U.S")) {
            Statement statement = JDBC.getConnection().createStatement();
            String getAllCitiesSQL = "SELECT * FROM first_level_divisions WHERE COUNTRY_ID = 1";
            ResultSet usCityResults = statement.executeQuery(getAllCitiesSQL);

            while (usCityResults.next()) {
                String city = usCityResults.getString("Division");
                citiesList.add(city);
                cityComboBox.setItems(citiesList);
            }
            statement.close();

        } else if(countrySelected.equals("UK")) {
            Statement statement = JDBC.getConnection().createStatement();
            String getAllCitiesSQL = "SELECT * FROM first_level_divisions WHERE COUNTRY_ID = 2";
            ResultSet ukCityResults = statement.executeQuery(getAllCitiesSQL);

            while (ukCityResults.next()) {
                String city = ukCityResults.getString("Division");
                citiesList.add(city);
                cityComboBox.setItems(citiesList);
            }
            statement.close();

        } else {
            Statement statement = JDBC.getConnection().createStatement();
            String getAllCitiesSQL = "SELECT * FROM first_level_divisions WHERE COUNTRY_ID = 3";
            ResultSet canadaCityResults = statement.executeQuery(getAllCitiesSQL);

            while (canadaCityResults.next()) {
                String city = canadaCityResults.getString("Division");
                citiesList.add(city);
                cityComboBox.setItems(citiesList);
            }
            statement.close();
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
	 * Cities Observable List
	 */
    ObservableList<String> citiesList = FXCollections.observableArrayList();
	/**
	 * Countries Observable List
	 */
    ObservableList<String> countriesList = FXCollections.observableArrayList("U.S", "Canada", "UK");

    /**
	 * Cancels Adding and Returns to Main Menu
     * @param actionEvent Handles the event when button is called
	 */
    public void onActionCancel(ActionEvent actionEvent) throws IOException {
        Alert alertForCancel = new Alert(Alert.AlertType.CONFIRMATION);
        alertForCancel.setTitle("Cancel");
        alertForCancel.setHeaderText("Sure you want to cancel?");
        alertForCancel.setContentText("This will clear all text fields");
        Optional<ButtonType> cancelSelection = alertForCancel.showAndWait();

        if(cancelSelection.isPresent() && cancelSelection.get() == ButtonType.OK) {
            buttonChanging(actionEvent, "/view/customersTable.fxml");
        }
    }
	
	/**
	 * Scene Variable
	 */
    Parent scene;
	/**
	 * Stage Variable
	 */
    Stage stage;

    /**
	 * Populates Country Combo Box
	 */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countryComboBox.setItems(countriesList);
    }

    public void onActionCityComboBox(ActionEvent actionEvent) throws SQLException {
        String citySelected = cityComboBox.getSelectionModel().getSelectedItem();

        getAllCitiesDivisionID(citySelected);
        DataProvider.divisionID = divisionIDFromCity;
        System.out.println(divisionIDFromCity);
    }

    /**
	 * Saves Customer on DB
     * @param actionEvent Handles the event when the button is called
	 */
    public void onActionSaveCustomer(ActionEvent actionEvent) throws IOException, SQLException {

        int customerID = 0;
        for (Customer customer : Customer.getGetAllCustomers()) {
            if (customer.getCustomerID() > customerID) {
                customerID = customer.getCustomerID();
            }
        }
        String customerName = nameTxtFld.getText();
        String customerAddress = addressTxtFld.getText();
        String customerPostalCode = postalCodeTxtFld.getText();
        String customerPhoneNumber = phoneTxtFld.getText();
        String customerCity = cityComboBox.getSelectionModel().getSelectedItem();
        String customerCountry = countryComboBox.getSelectionModel().getSelectedItem();

        if (nameNotNull(customerName) && addressNotNull(customerAddress) && postalCodeNotNull(customerPostalCode) && phoneNotNull(customerPhoneNumber) && countryNotNull(customerCountry) && cityNotNull(customerCity)) {

            DataBaseQueries.insertIntoCustomersTable(customerName, customerAddress,customerPhoneNumber, customerPostalCode, DataProvider.divisionID);
            Alerts.alertDisplays(6);
            buttonChanging(actionEvent, "/view/customersTable.fxml");
        }
    }

    /**
	 * Gets Cities With DivisionID
     * @param comboBoxSelection Combo box selection
	 */
    public void getAllCitiesDivisionID(String comboBoxSelection) throws SQLException {
        Statement state = JDBC.getConnection().createStatement();
        String getAllCitiesDivisionIDSQL = "SELECT Division_ID FROM first_level_divisions WHERE Division='" + comboBoxSelection + "'";
        ResultSet result = state.executeQuery(getAllCitiesDivisionIDSQL);

        while(result.next()) {
            divisionIDFromCity = result.getInt("Division_ID");
        }
    }
	
	/**
	 * Displays Error When The City Combo Box is Empty
     * @param city The text in the city combo box
	 */
    public boolean cityNotNull(String city) {
        if (cityComboBox.getSelectionModel().getSelectedItem() == null) {
            Alerts.alertDisplays(3);
            return false;
        }
        return true;
    }
    /**
	 * Displays Error When The Address Field is Empty
     * @param address The text in the address
	 */
    public boolean addressNotNull(String address) {
        if (addressTxtFld.getText().isEmpty()) {
            Alerts.alertDisplays(2);
            return false;
        }
        return true;
    }
	/**
	 * Displays Error When The Country Combo Box is Empty
     * @param country The text in the country combo box
	 */
    public boolean countryNotNull(String country) {
        if (countryComboBox.getSelectionModel().getSelectedItem() == null) {
            Alerts.alertDisplays(10);
            return false;
        }
        return true;
    }
    /**
	 * Displays Error When The PostalCode Field is Empty
     * @param postalCode The text in the postalCode
	 */
    public boolean postalCodeNotNull(String postalCode) {
        if (postalCodeTxtFld.getText().isEmpty()) {
            Alerts.alertDisplays(4);
            return false;
        }
        return true;
    }
    /**
	 * Displays Error When The Phone Field is Empty
     * @param phone The text in the phone
	 */
    public boolean phoneNotNull(String phone) {
        if (phoneTxtFld.getText().isEmpty()) {
            Alerts.alertDisplays(5);
            return false;
        }
        return true;
    }
    /**
	 * Displays Error When The Name Field is Empty
     * @param name The text in the name field
	 */
    public boolean nameNotNull(String name) {
        if (nameTxtFld.getText().isEmpty()) {
            Alerts.alertDisplays(1);
            return false;
        }
        return true;
    }
}