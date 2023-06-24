package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.JDBC;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Model for Customers class
 * @author Seyed Hassanzadeh
 */
public class Customer {
    /**
	 * CustomerID
	 */
    private int customerID;
    /**
	 * Customer Name
	 */
    private String customerName;
    /**
	 * Customer Address
	 */
    private String address;
    /**
	 * Customer City
	 */
    private String city;
    /**
	 * Customer Postal Code
	 */
    private String postalCode;
    /**
	 * Customer Phone number
	 */
    private String phoneNumber;
    /**
	 * Customer Country
	 */
    private String country;
    /**
	 * Observable List of Customer
	 */
    public static ObservableList<Customer> allCustomersList = FXCollections.observableArrayList();
    /** 
	 * Constructor for Customers 
     * @param customerID customerID
     * @param customerName customer Name
     * @param address address
     * @param city city
     * @param country country
     * @param postal_Code postal Code
     * @param phoneNumber  phone Number
	 */
    public Customer(int customerID, String customerName, String address, String city, String postal_Code, String phoneNumber, String country) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.address = address;
        this.city = city;
        this.country = country;
        this.postalCode = postal_Code;
        this.phoneNumber = phoneNumber;
    }
    /**
	 * Getter for CustomerID
     * @return customerID
	 */
    public int getCustomerID() {
        return customerID;
    }
	/** 
	 * Getter for Customer Name
     * @return customer Name
	 */
    public String getCustomerName() {
        return customerName;
    }
	/** 
	 * Getter for Country
     * @return country
	 */
    public String getCountry() {
        return country;
    }
	/** 
	 * Getter for Postal Code
     * @return Postal Code
	 */
    public String getPostalCode() {
        return postalCode;
    }
	/** 
	 * Getter for City
     * @return city
	 */
    public String getCity() {
        return city;
    }
	/** 
	 * Getter for Phone Number
     * @return Phone number
	 */
    public String getPhoneNumber() {
        return phoneNumber;
    }
	/** 
	 * Getter for Address
     * @return address
	 */
    public String getAddress() {
        return address;
    }
	/** 
	 * Getter for All Customers
     * @return allCustomersList
	 */
    public static ObservableList<Customer> getGetAllCustomers() throws SQLException {

        Statement statement = JDBC.getConnection().createStatement();
        String customerInfoSQL = "SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, countries.Country, first_level_divisions.*" +
                                "FROM customers " +
                                "INNER JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID " +
                                "INNER JOIN countries ON first_level_divisions.COUNTRY_ID = countries.Country_ID";

        ResultSet customerInfoResults = statement.executeQuery(customerInfoSQL);

        while(customerInfoResults.next()) {
            Customer customer = new Customer(customerInfoResults.getInt("Customer_ID"),
                                            customerInfoResults.getString("Customer_Name"),
                                            customerInfoResults.getString("Address"),
                                            customerInfoResults.getString("Division"),
                                            customerInfoResults.getString("Postal_Code"),
                                            customerInfoResults.getString("Phone"),
                                            customerInfoResults.getString("Country"));
            allCustomersList.add(customer);
        }
        return allCustomersList;
    }
	
	/** 
	 * Setter For Country
	 */
    public void setCountry(String country) {
        this.country = country;
    }
	
	/** 
	* Setter For City
	*/
    public void setCity(String city) {
        this.city = city;
    }
	
	/** 
	 * Setter For Phone Number
	 */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
	
	/** 
	 * Setter For Postal Code
	 */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /** 
	 * Setter For Customer ID
	 */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
	
	/** 
	 * Setter For Address
	 */
    public void setAddress(String address) {
        this.address = address;
    }

    /** 
	 * Setter For CustomerName
	 */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}