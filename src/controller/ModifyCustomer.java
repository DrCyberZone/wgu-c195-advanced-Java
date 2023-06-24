package controller;

import util.DataBaseQueries;
import util.DataProvider;
import util.JDBC;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import model.Customer;

/**
 * Modify Customers Controller Class
 * @author Seyed Hassanzadeh
 */

public class ModifyCustomer implements Initializable {

    /**
	 * City Combo Box
	 */
    public ComboBox<String> cityComboBox;
    /**
	 * Country Combo Box
	 */
    public ComboBox<String> countryComboBox;
	/**
	 * Save Button
	 */
    public Button saveCustomerBtn;
    /**
	 * Cancel Button
	 */
    public Button cancelBtn;
    /**
	 * DivisionID
	 */
    public int divisionIDFromCity;
	/**
	 * Name Text Field
	 */
    public TextField nameTxtFld;
    /**
	 * Address Text Field
	 */
    public TextField addressTxtFld;
    /**
	 * Postal Code Text Field
	 */
    public TextField postalCodeTxtFld;
    /**
	 * Phone Text Field
	 */
    public TextField phoneTxtFld;
    /**
	 * CustomerID Text Field
	 */
    public TextField customerIdTextFld;
    /**
	 * Selects Customer
	 */
    Customer highlightedCustomer;
	/**
	 * Countries Observable List
	 */
    ObservableList<String> countriesList = FXCollections.observableArrayList("U.S", "Canada", "UK");
	/**
	 * Cities Observable List
	 */
    ObservableList<String> citiesList = FXCollections.observableArrayList();

    /**
	 * Populates the Countries Combo Box
	 */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countryComboBox.setItems(countriesList);
        highlightedCustomer = CustomersTable.getHighlightedCustomer();

        customerIdTextFld.setText(String.valueOf(highlightedCustomer.getCustomerID()));
        nameTxtFld.setText(highlightedCustomer.getCustomerName());
        addressTxtFld.setText(highlightedCustomer.getAddress());
        postalCodeTxtFld.setText(highlightedCustomer.getPostalCode());
        phoneTxtFld.setText(highlightedCustomer.getPhoneNumber());
        countryComboBox.setValue(highlightedCustomer.getCountry());
        cityComboBox.setValue(highlightedCustomer.getCity());
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
	 * Filters the City Combo Box
     * @param actionEvent Handles the action event
	 */
    public void onActionComboBox(ActionEvent actionEvent) throws SQLException {
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
	 * Cancels Modify Customer
     * @param actionEvent The action event
	 */
    public void onActionCancel(ActionEvent actionEvent)  throws IOException {
        Alert alertForCancel = new Alert(Alert.AlertType.CONFIRMATION);
        alertForCancel.setTitle("Cancel");
        alertForCancel.setHeaderText("Sure you want to cancel?");
        alertForCancel.setContentText("This will not change any information");
        Optional<ButtonType> cancelSelection = alertForCancel.showAndWait();

        if(cancelSelection.isPresent() && cancelSelection.get() == ButtonType.OK) {
            buttonChanging(actionEvent, "/view/customersTable.fxml");
        }
    }

	/**
	 * Gets the divisionID
     * @param comboBoxSelection combo box selection
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
	 * Gets the divisionID
     * @param actionEvent The action event
	 */
    public void onActionCityComboBox(ActionEvent actionEvent) throws SQLException {
        String citySelected = cityComboBox.getSelectionModel().getSelectedItem();
        getAllCitiesDivisionID(citySelected);
        DataProvider.divisionID = divisionIDFromCity;
    }

    /**
	 * Saves Customer to DB
     * @param actionEvent The action event
	 */
    public void onActionSaveCustomer(ActionEvent actionEvent) throws IOException, SQLException {
        String customerName = nameTxtFld.getText();
        String customerAddress = addressTxtFld.getText();
        String customerPostalCode = postalCodeTxtFld.getText();
        String customerPhone = phoneTxtFld.getText();

        DataBaseQueries.updateToCustomersTable(highlightedCustomer.getCustomerID(), customerName, customerAddress, customerPostalCode, customerPhone, DataProvider.divisionID);
        buttonChanging(actionEvent, "/view/customersTable.fxml");
    }
}