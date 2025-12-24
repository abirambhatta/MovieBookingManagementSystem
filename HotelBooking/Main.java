package HotelBooking;

import HotelBooking.controller.LoginController;
import HotelBooking.view.LoginView;

/**
 * Main class - Entry point of the Hotel Booking Management System
 * Initializes and displays the login screen
 */
public class Main {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                LoginView view = new LoginView();
                new LoginController(view);
                view.setVisible(true);
            }
        });
    }
}
