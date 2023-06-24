package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;
import util.JDBC;
import javafx.event.ActionEvent;
import model.User;
import javafx.fxml.FXMLLoader;
import util.LoginAttemptTracker;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.Alerts;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Main screen class
 * @author Seyed Hassanzadeh
 */
 
public class Controller implements Initializable {

    /**
	 * Exit Button
	 */
    public Button exitBtn;
    /**
	 * Username Label
	 */
	public Label usernameLabel;
    /**
	 * Password Label
	 */
	public Label passwordLabel;
	/**
	 * Username Text Field
	 */
    public TextField usernameTxtField;
	/**
	 * Password Text Field
	 */
    public PasswordField passwordTxtField;
    /**
	 * Login Button
	 */
    public Button loginButton;
    /**
	 * ZoneId Label
	 */
    public Label zoneID;

    /**
	 * Stage Variable
	 */
    Stage stage;
	/**
	 * Scene Variable
	 */
    Parent scene;
	
	public static String titleForLogin;
    public static String headerForLogin;
    public static String contextForLogin;
	public static String titleForUserID;
    public static String headerForUserID;
    public static String contextForUserID;
	
	/**
	 * Exits the Program
     * @param actionEvent Handles the button being called 
	 */
    public void onActionExit(ActionEvent actionEvent) {
        System.exit(0);
    }
	
	/**
	 * Gets UserId From DB
     * @param username username entered at login
	 */
    public static int getUserIdFromUsername(String username) throws SQLException {
        Statement statement = JDBC.getConnection().createStatement();
        String sqlUsername = "SELECT User_ID, User_Name FROM users WHERE User_Name ='" + username + "'";
        ResultSet results = statement.executeQuery(sqlUsername);

        while(results.next()) {
            if(results.getString("User_Name").equals(username)) {
                return results.getInt("User_ID");
            }
        }
        statement.close();
        Alerts.errorAlert(titleForUserID, headerForUserID, contextForUserID);
        return -1;
    }
	
	/**
	 * Gets Passwords From DB
     * @param password password entered at login
	 */
    private boolean getPassword(String password) throws SQLException {
        Statement statement = JDBC.getConnection().createStatement();
        String sqlPassword = "SELECT Password FROM users WHERE Password ='" + password + "'";
        ResultSet results = statement.executeQuery(sqlPassword);

        while(results.next()) {
            if(results.getString("Password").equals(password)) {
                return true;
            }
        }
        statement.close();
        return false;
    }
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ZoneId zone = ZoneId.systemDefault();
        zoneID.setText(zone.toString());

        Locale locale = Locale.getDefault();
        ResourceBundle rsBundle = ResourceBundle.getBundle("LanguageBundles/shass40", locale);

        if(locale.getLanguage().equals("fr")) {
            this.usernameTxtField.setPromptText(rsBundle.getString("usernameFieldPromptText"));
            this.passwordTxtField.setPromptText(rsBundle.getString("passwordFieldPromptText"));
            this.usernameLabel.setText(rsBundle.getString("username"));
            this.passwordLabel.setText(rsBundle.getString("password"));
            this.loginButton.setText(rsBundle.getString("loginButtonText"));
            this.exitBtn.setText(rsBundle.getString("exitBtnText"));
        }
        titleForUserID = rsBundle.getString("titleForUserID");
        headerForUserID = rsBundle.getString("headerForUserID");
        contextForUserID = rsBundle.getString("contextForUserID");
        titleForLogin = rsBundle.getString("titleForLogin");
        headerForLogin = rsBundle.getString("headerForLogin");
        contextForLogin = rsBundle.getString("contextForLogin");
    }
	
	/**
	 * Gets and Checks Username
     * @param username username entered at login
	 */
    private boolean getUsername(String username) throws SQLException {
        Statement statement = JDBC.getConnection().createStatement();
        String sqlUsername = "SELECT User_Name FROM users WHERE User_Name ='" + username + "'";
        ResultSet results = statement.executeQuery(sqlUsername);

        while(results.next()) {
            if(results.getString("User_Name").equals(username)) {
                return true;
            }
        }
        statement.close();
        return false;
    }

    /**
	 * Login the Users
     * Logins are in login_activity.txt file
     * @param actionEvent Handles the event when the button is called
	 */
    public void onActionLogin(ActionEvent actionEvent) throws IOException, SQLException {
        String username = usernameTxtField.getText();
        String password = passwordTxtField.getText();
        User.username = username;

        getUsername(username);
        getPassword(password);

        if (getUsername(username) && getPassword(password)) {
            LoginAttemptTracker.logAttempt(username, true, "You are now logged in.");
            getUserIdFromUsername(username);

            stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/mainMenu.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        } else {
            LoginAttemptTracker.logAttempt(username, false, "Login failed, please try again.");
            Alerts.errorAlert(titleForLogin, headerForLogin, contextForLogin);
        }
    }
}