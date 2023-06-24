package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Alerts;
import model.Customer;
import util.DataBaseQueries;
import util.JDBC;

/**
 * Customers Table Controller Class
 * @author Seyed Hassanzadeh
 */
 
public class CustomersTable implements Initializable {

    /**
	 * Modify Button
	 */
    public Button modifyBtn;
    /**
	 * Delete Button
	 */
    public Button deleteBtn;
    /**
	 * Main Menu Button
	 */
    public Button mainMenuBtn;
    /**
	 * Selected Customer
	 */
    private static Customer highlightedCustomer;
    /**
	 * Customer Address Table Column
	 */
    public TableColumn<Customer, String> customersTblAddress;
    /**
	 * Customer City Table Column
	 */
    public TableColumn<Customer, String> customersTblCity;
    /**
	 * Customer County Table Column
	 */
    public TableColumn<Customer, String> customersTblCountry;
    /**
	 * Customer Table View
	 */
    public TableView<Customer> customersTblView;
    /**
	 * CustomerId Table Column
	 */
    public TableColumn<Customer, Integer> customersTblID;
    /**
	 * Customer Name Table Column
	 */
    public TableColumn<Customer, String> customersTblName;
	/**
	 * Customer Postal Code Table Column
	 */
    public TableColumn<Customer, String> customersTblPostalCode;
    /**
	 * Customer Phone Table Column
	 */
    public TableColumn<Customer, String> customersTblPhone;
    /**
	 * Add Button
	 */
    public Button addBtn;
    
	/**
	 * Getter for Highlighted Customer.
	 */
    public static Customer getHighlightedCustomer() {
        return highlightedCustomer;
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
	 * Goes to Modify Customer Screen.
     * @param actionEvent Handles when button called. 
	 */
    public void onActionModify(ActionEvent actionEvent) throws IOException {
        highlightedCustomer = customersTblView.getSelectionModel().getSelectedItem();

        if(highlightedCustomer == null) {
            Alerts.errorAlert( "ERROR", "No highlighted customer", "Please select a customer to modify");
        } else {
            buttonChanging(actionEvent, "/view/modifyCustomer.fxml");
        }
    }
	
	/**
	 * Goes to Add Customer Screen.
     * @param actionEvent Handles when button called.
	 */
    public void onActionAdd(ActionEvent actionEvent) throws IOException {
        buttonChanging(actionEvent, "/view/addCustomer.fxml");
    }
	
	/**
	 * Populates Customer Table View
	 */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Customer.getGetAllCustomers().clear();
            customersTblView.setItems(Customer.getGetAllCustomers());
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        customersTblID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customersTblName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customersTblAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        customersTblCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        customersTblCountry.setCellValueFactory(new PropertyValueFactory<>("country"));
        customersTblPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customersTblPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }
	
	/**
	 * Deletes Highlighted Customer
     * @param actionEvent Handles when button called. 
	 */
    public void onActionDelete(ActionEvent actionEvent) throws SQLException, IOException {
        Customer highlightedCustomer = customersTblView.getSelectionModel().getSelectedItem();

        if(highlightedCustomer == null) {
            Alerts.alertDisplays(9);
        } else if(getCustomerAptCount(highlightedCustomer.getCustomerID()) == 0){
            Alert alertForDelete = new Alert(Alert.AlertType.CONFIRMATION);
            alertForDelete.setHeaderText("Sure you want to delete this customer?");
            alertForDelete.setContentText("Deleting the customer will remove them and their appointments");
            Optional<ButtonType> deleteResult = alertForDelete.showAndWait();

            if(deleteResult.isPresent() && deleteResult.get() == ButtonType.OK) {
                DataBaseQueries.deleteFromCustomersTable(highlightedCustomer.getCustomerID());
                Alerts.alertDisplays(11);
                buttonChanging(actionEvent, "/view/CustomersTable.fxml");
            }
        }
    }

    /**
	 * Goes to The New Screen
     * @param actionEvent The action event.
     * @param resourcesString link to the new screen. 
	 */
    public void buttonChanging(ActionEvent actionEvent, String resourcesString) throws IOException {
        stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource(resourcesString));
        stage.setScene(new Scene(scene));
        stage.show();
    }
	
	/**
	 * Goes to The Main Menu Customer Screen.
     * @param actionEvent Handles when button called. 
	 */
    public void onActionMainMenu(ActionEvent actionEvent) throws IOException {
        buttonChanging(actionEvent, "/view/mainMenu.fxml");
    }

    /**
	 * Counts Appointments Per Customer.
     * If that count is 1 or greater won't delete.
     * @param customerID Customer id. 
	 */
    public static int getCustomerAptCount(int customerID) throws SQLException {

        Statement customerAptCount = JDBC.getConnection().createStatement();
        String modifySQL =
                "SELECT COUNT(Appointment_ID) AS Count " +
                "FROM appointments " +
                "INNER JOIN customers ON customers.Customer_ID = appointments.Customer_ID " +
                "WHERE customers.Customer_ID=" + customerID;

        ResultSet aptCount = customerAptCount.executeQuery(modifySQL);

        if(aptCount.next() && aptCount.getInt("Count") > 0) {
            Alerts.errorAlert("Cannot Delete Customer",
                    "Customer has existing appointments",
                    "Please delete all appointments associated with this customer before trying to delete.");
            return -1;
        }
        return 0;
    }
}