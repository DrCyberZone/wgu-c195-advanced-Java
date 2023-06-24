package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.JDBC;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

/**
 * Model for Appointments class
 * @author Seyed Hassanzadeh
 */
public class Appointments {

    /** 
	 * Appointments Observable List
	 */
    public static ObservableList<Appointments> allAppointmentsList = FXCollections.observableArrayList();
    /** 
	 * Contacts Observable List
	 */
    public static ObservableList<Appointments> selectedContactNameList = FXCollections.observableArrayList();
	/**
	 * AppointmentID
	 */
    private int appointmentId;
    /** 
	 * Appointment Start Time
	 */
    private LocalDateTime start;
    /** 
	 * Appointment End Time
	 */
    private LocalDateTime end;
    /** 
	 * CustomerID
	 */
    private int customerId;
    /** 
	 * UserID
	 */
    private int userId;
    /** 
	 * Contact Name
	 */
    private String contactName;
	/** 
	 * Appointment Title
	 */
    private String title;
    /** 
	 * Appointment Description
	 */
    private String description;
    /** 
	 * Appointment Location
	 */
    private String location;
    /** 
	 * Appointment Type
	 */
    private String type;
    /**
	 * Constructor
	 */
    public Appointments() {}
    /** 
	 * Appointment constructor
     * @param title title
     * @param start start
     * @param appointmentId appointmentId
     * @param contactId contactId
     * @param customerId customerId
     * @param description description
     * @param location location
     * @param type type
     * @param userId userId
     * @param end end
	 */
    public Appointments(int appointmentId, String title, String description, String location, String contactId, String type,
                        LocalDateTime start, LocalDateTime end, int customerId, int userId) {
        this.appointmentId = appointmentId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customerId = customerId;
        this.userId = userId;
        this.contactName = contactId;
    }
    /** 
	 * Getter for userID
     * @return UserID
	 */
    public int getUserId() {return userId;
    }
	/** 
	 * Getter for End Time
     * @return end
	 */
    public LocalDateTime getEnd() {return end;
    }
	/** 
	 * Getter for CustomerID
     * @return Customer ID
	 */
    public int getCustomerId() {return customerId;
    }
	/** 
	 * Getter for AppointmentID
     * @return appointment ID
	 */
    public int getAppointmentId() {return appointmentId;
    }
	/** 
	 * Getter for Title
     * @return title
	 */
    public String getTitle() {return title;
    }
	/** 
	 * Getter for Type
     * @return type
	 */
    public String getType() {return type;
    }
	/** 
	 * Getter for Description
     * @return description
	 */
    public String getDescription() {return description;
    }
	/** 
	 * Getter for Contact Name
     * @return Contact name
	 */
    public String getContactName() {return contactName;
    }
	/** 
	 * Getter for Location
     * @return location
	 */
    public String getLocation() {return location;
    }
	/** 
	 * Getter for Start Time
     * @return start
	 */
    public LocalDateTime getStart() {return start;}
	/** 
	 * Getter for All Appointments
     * @return allAppointmentsList
	 */
    public static ObservableList<Appointments> getGetAllAppointments() throws SQLException {

        Statement statement = JDBC.getConnection().createStatement();
        String appointmentInfoSQL = "SELECT appointments.*, contacts.* FROM appointments INNER JOIN contacts ON appointments.Contact_ID = contacts.Contact_ID";
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
            allAppointmentsList.add(appointments);
        }
        return allAppointmentsList;
    }
	/** 
	 * Setter for Contact Name
	 */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
	/** 
	 * Setter for CustomerID
	 */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
	/** 
	 * Setter for UserID
	 */
    public void setUserId(int userId) {
        this.userId = userId;
    }
	/** 
	 * Setter for Location
	 */
    public void setLocation(String location) {
        this.location = location;
    }
	/** 
	 * Setter for End Time
	 */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
    /** 
	 * Setter for Title
	 */
    public void setTitle(String title) {
        this.title = title;
    }
	/** 
	 * Setter for AppointmentID
	 */
    public void setAppointmentId(int appointmentId) {this.appointmentId = appointmentId;}
    /** 
	 * Setter for Description
	 */
    public void setDescription(String description) {
        this.description = description;
    }
    /** 
	 * Setter for Start Time
	 */
    public void setStart(LocalDateTime start) {this.start = start;}	
}