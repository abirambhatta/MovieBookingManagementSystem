package MovieBooking;

import MovieBooking.controller.AuthenticationController;
import MovieBooking.view.AuthenticationView;

/**
 * Main class - Entry point for Movie Booking Management System
 * Starts the application with Authentication screen using CardLayout
 */
public class Main {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        // Start application with authentication screen
        AuthenticationView authView = new AuthenticationView();
        new AuthenticationController(authView);
        authView.setVisible(true);
    }
}