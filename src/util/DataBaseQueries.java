package util;

import model.Alerts;
import java.sql.*;

/**
 * @author Seyed Hassanzadeh
 */
public class DataBaseQueries {

    /**
	 * Adds to The Appointments Table
     * @param end End
     * @param start Start
     * @param title Title
     * @param contactId ContactId
     * @param customerId CustomerId
	 * @param userId USERID
     * @param type Type
     * @param location Location
     * @param description Description
	 */
    public static int insertAppointment(String title, String description, String location, String type,
                                        Timestamp start, Timestamp end, int customerId, int userId, int contactId) throws SQLException {
        String insertAptSQL = "INSERT INTO Appointments (Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID) " +
                               "VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = JDBC.getConnection().prepareStatement(insertAptSQL);

        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, type);
        ps.setTimestamp(5, start);
        ps.setTimestamp(6, end);
        ps.setInt(7, customerId);
        ps.setInt(8, userId);
        ps.setInt(9, contactId);

        int rowsAffectedAppointmentInsert = ps.executeUpdate();

        ps.close();
        return rowsAffectedAppointmentInsert;
    }
	/**
	 * Deletes Appointments
     * @param appointmentId customerID
	 */
    public static int deleteFromAppointmentsTable(int appointmentId) throws SQLException {

        String deleteAppointmentSQL = "DELETE FROM appointments WHERE Appointment_ID = ?";
        PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(deleteAppointmentSQL);

        preparedStatement.setInt(1, appointmentId);

        int rowsDeletedForAppointments = preparedStatement.executeUpdate();

        preparedStatement.close();
        return rowsDeletedForAppointments;
    }
    /**
	 * Updates customers table
     * @param divisionID divisionID
     * @param Phone Phone
     * @param postalCode postalCode
     * @param customerID customerID
	 * @param customerName customer Name
     * @param address address
	 */
    public static int updateToCustomersTable(int customerID, String customerName, String address, String postalCode, String Phone, int divisionID) throws SQLException {

        String modifySQL = "UPDATE customers SET Customer_Name=?, Address=?, Phone=?, Postal_Code=?, Division_ID=? WHERE Customer_ID=?";
        PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(modifySQL);

        preparedStatement.setInt(6, customerID);
        preparedStatement.setString(1, customerName);
        preparedStatement.setString(2, address);
        preparedStatement.setString(3, Phone);
        preparedStatement.setString(4, postalCode);
        preparedStatement.setInt(5, divisionID);

        int rowsUpdates = preparedStatement.executeUpdate();

        if (rowsUpdates == 1) {
            Alerts.alertDisplays(7);
        }
        preparedStatement.close();
        return rowsUpdates;
    }
    /**
	 * Deletes Customers
     * @param customerID customerID
	 */
    public static int deleteFromCustomersTable(int customerID) throws SQLException {

        String modifySQL = "DELETE FROM customers WHERE Customer_ID = ?";
        PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(modifySQL);
        preparedStatement.setInt(1, customerID);
        int rowsDeleted = preparedStatement.executeUpdate();
        preparedStatement.close();
        return rowsDeleted;
    }
    /**
	 * Updates selected appointment id
     * @param description description
     * @param location location
     * @param type type
     * @param userId user id
	 * @param appointmentID appointmentID
     * @param customerId customerId
     * @param contactId contactId
     * @param title title
     * @param start start
     * @param end end
	 */
    public static int updateAppointment(int appointmentID, String title, String description, String location, String type,
                                        Timestamp start, Timestamp end, int customerId, int userId, int contactId) throws SQLException {
        String updateAptSQL = "UPDATE appointments SET Title=?, Description=?, Location=?, Type=?, Start=?, End=?, " +
                                    "Customer_ID=?, User_ID=?, Contact_ID=? " +
                                "WHERE Appointment_ID=?";
        PreparedStatement ps = JDBC.getConnection().prepareStatement(updateAptSQL);

        ps.setInt(10, appointmentID);
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, type);
        ps.setTimestamp(5, start);
        ps.setTimestamp(6, end);
        ps.setInt(7, customerId);
        ps.setInt(8, userId);
        ps.setInt(9, contactId);
        int rowsAffectedAppointmentInsert = ps.executeUpdate();

        ps.close();
        return rowsAffectedAppointmentInsert;
    }
	/**
	 * Adds Customers to The Database
	 * @param divisionID DivisionID
     * @param Phone Phone
     * @param postalCode PostalCode
     * @param customerName Customer Name
     * @param address Address
	 */
    public static int insertIntoCustomersTable(String customerName, String address, String Phone, String postalCode, int divisionID) throws SQLException {
        String sql = "INSERT INTO customers (Customer_Name, Address, Phone, Postal_Code, Division_ID) VALUES(?,?,?,?,?)";
        PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);

        ps.setString(1, customerName);
        ps.setString(2, address);
        ps.setString(3, Phone);
        ps.setString(4, postalCode);
        ps.setInt(5, divisionID);

        int rowsAffected = ps.executeUpdate();

        ps.close();
        return rowsAffected;
    }
}