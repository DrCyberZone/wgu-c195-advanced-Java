package model;

import javafx.scene.control.Alert;

/**
 * Model for Alerts class
 * @author Seyed Hassanzadeh
 */
public class Alerts {

    /** 
	 * Different Alerts Display
     * @param alertType Alert type
	 */
    public static void alertDisplays(int alertType) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        Alert alertForSave = new Alert(Alert.AlertType.INFORMATION);

        switch (alertType) {
            case 1:
                alert.setTitle("Error");
                alert.setHeaderText("Please Complete All Fields");
                alert.setContentText("Name is missing!");
                alert.showAndWait();
                break;
            case 2:
                alert.setTitle("Error");
                alert.setHeaderText("Please Complete All Fields");
                alert.setContentText("Address is missing!");
                alert.showAndWait();
                break;
            case 3:
                alert.setTitle("Error");
                alert.setHeaderText("Please Complete All Fields");
                alert.setContentText("Select the city!");
                alert.showAndWait();
                break;
            case 4:
                alert.setTitle("Error");
                alert.setHeaderText("Please Complete All Fields");
                alert.setContentText("Postal code is missing!");
                alert.showAndWait();
                break;
            case 5:
                alert.setTitle("Error");
                alert.setHeaderText("Please Complete All Fields");
                alert.setContentText("Phone number is missing!");
                alert.showAndWait();
                break;
            case 6:
                alertForSave.setTitle("Saved Customer");
                alertForSave.setHeaderText("Database is Updated");
                alertForSave.setContentText("New customer has been saved");
                alertForSave.showAndWait();
                break;
            case 7:
                alertForSave.setTitle("Modified Customer");
                alertForSave.setHeaderText("Customer Modified");
                alertForSave.setContentText("Customer has been saved");
                alertForSave.showAndWait();
                break;
            case 8:
                alertForSave.setTitle("Deleted Customer");
                alertForSave.setHeaderText("Are You Sure You Want To Delete This?");
                alertForSave.setContentText("Customer appointments will be deleted");
                alertForSave.showAndWait();
                break;
            case 9:
                alert.setTitle("No Highlighted Customer");
                alert.setHeaderText("No Highlighted Customer");
                alert.setContentText("Please highlight a customer");
                alert.showAndWait();
                break;
            case 10:
                alert.setTitle("No Country Selected");
                alert.setHeaderText("Country is Missing!");
                alert.setContentText("Select country");
                alert.showAndWait();
                break;
            case 11:
                alert.setTitle("Customer Deleted");
                alert.setHeaderText("Customer Has Been Deleted Successfully.");
                alert.setContentText("Customer information and appointments have been deleted.");
                alert.showAndWait();
                break;
            case 12:
                alert.setTitle("User Id Not Found");
                alert.setHeaderText("Cannot Find User_Id");
                alert.setContentText("Enter valid username");
                alert.showAndWait();
                break;
            case 13:
                alert.setTitle("Error");
                alert.setHeaderText("Please Complete All Fields");
                alert.setContentText("Title is missing!");
                alert.showAndWait();
                break;
            case 14:
                alert.setTitle("Error");
                alert.setHeaderText("Please Complete All Fields");
                alert.setContentText("Description is missing!");
                alert.showAndWait();
                break;
            case 15:
                alert.setTitle("Error");
                alert.setHeaderText("Please Complete All Fields");
                alert.setContentText("Location is missing!");
                alert.showAndWait();
                break;
            case 16:
                alert.setTitle("Error");
                alert.setHeaderText("Please Complete All Fields");
                alert.setContentText("Type is missing!");
                alert.showAndWait();
                break;
            case 17:
                alert.setTitle("Error");
                alert.setHeaderText("Please Complete All Fields");
                alert.setContentText("Date picker is missing!");
                alert.showAndWait();
                break;
            case 18:
                alert.setTitle("Error");
                alert.setHeaderText("Please Complete All Fields");
                alert.setContentText("Start time is missing!");
                alert.showAndWait();
                break;
            case 19:
                alert.setTitle("Error");
                alert.setHeaderText("Please Complete All Fields");
                alert.setContentText("End time is missing!");
                alert.showAndWait();
                break;
            case 20:
                alert.setTitle("Error");
                alert.setHeaderText("Please Complete All Fields");
                alert.setContentText("Existing customer is missing!");
                alert.showAndWait();
                break;
            case 21:
                alert.setTitle("Error");
                alert.setHeaderText("Please Complete All Fields");
                alert.setContentText("User ID is missing!");
                alert.showAndWait();
                break;
            case 22:
                alert.setTitle("Error");
                alert.setHeaderText("Please Complete All Fields");
                alert.setContentText("Contact is missing!");
                alert.showAndWait();
                break;
            case 23:
                alertForSave.setTitle("Saved Appointment");
                alertForSave.setHeaderText("Appointment is Saved");
                alertForSave.setContentText("New appointment has been saved");
                alertForSave.showAndWait();
                break;
            case 24:
                alert.setTitle("Error");
                alert.setHeaderText("Appointment Needs To Start First");
                alert.setContentText("End time must be after start time.");
                alert.showAndWait();
                break;
            case 25:
                alert.setTitle("Error");
                alert.setHeaderText("Appointment Needs To Start Before End Time.");
                alert.setContentText("Please select the end time after the start time.");
                alert.showAndWait();
                break;
            case 26:
                alert.setTitle("Error");
                alert.setHeaderText("Appointment Collision");
                alert.setContentText("Please select another time. Appointment time is not available.");
                alert.showAndWait();
                break;
            case 28:
                alert.setTitle("Error");
                alert.setHeaderText("Please Complete All Fields");
                alert.setContentText("Fields cannot be empty");
                alert.showAndWait();
                break;
            case 29:
                alert.setTitle("Error");
                alert.setHeaderText("Please Check Business Hours");
                alert.setContentText("Appointment must be within business hours.");
                alert.showAndWait();
                break;
        }
    }
	
	/**
	 * Informative Alert Display.
     * @param title Title for information alert
     * @param context Context for information alert
     * @param header  header for information alert
	 */
    public static void informationAlert(String title, String header, String context) {
        Alert error = new Alert(Alert.AlertType.INFORMATION);
        error.setTitle(title);
        error.setHeaderText(header);
        error.setContentText(context);
        error.showAndWait();
    }
	
	/** 
	 * Errors Alert Display.
     * Lambda sets the title for error
     * @param title Title for error
     * @param context Context for error
     * @param header  header for error
	 */
    public static void errorAlert(String title, String header, String context) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        final Runnable runnable = () -> error.setTitle(title);
        runnable.run();
        error.setHeaderText(header);
        error.setContentText(context);
        error.showAndWait();
    }
}