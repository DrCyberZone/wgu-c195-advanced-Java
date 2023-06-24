package sample;

import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import util.JDBC;
import java.sql.SQLException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class creates the Scheduling Application System 
 * @author Seyed Hassanzadeh
 * Student ID: 001532626
 */
public class Main extends Application {
    /**
     * Calls for JDBC and Logger
     * Runs Args Then Close JDBC Connections
     */
    public static void main(String[] args) throws SQLException {
        JDBC.startConnection();
        launch(args);
        JDBC.closeConnection();
    }
	/**
	 *
	 * Sets The Main Stage
	 */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../view/sample.fxml"));
        primaryStage.setTitle("Scheduling Application - Advanced Java Presentation");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}