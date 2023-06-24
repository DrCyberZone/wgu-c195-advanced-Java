package util;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class LoginAttemptTracker {

    /**
	 * Login Attempt Tracker
	 */
    public static void logAttempt(String username, boolean loggedIn, String message) {
        try (FileWriter fileWriter = new FileWriter(FILE_NAME, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
            printWriter.println("Username " + username + " was a" + (loggedIn ? " success!" : " failure.") + " " + message + " " + Instant.now().toString());
        } catch (IOException e) {
            System.out.println("Log In Error: " + e.getMessage());
        }
    }
	
	/**
	 * File Name
	 */
    private static final String FILE_NAME = "login_activity.txt";

    /**
	 * Basic Constructor
	 */
    public LoginAttemptTracker() {}
}