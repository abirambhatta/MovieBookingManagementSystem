package filmvault.model;

import javax.swing.JOptionPane;
import javax.swing.JFrame;

public class validation {
    
    public static boolean validateLogin(String identifier, String password, JFrame view) {
        if (identifier.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Email/Username and Password are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!isValidEmail(identifier) && !isValidUsername(identifier)) {
            JOptionPane.showMessageDialog(view, "Please enter a valid email or username.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public static boolean validateSignUp(String username, String email, String password, String confirm, JFrame view) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(view, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!isValidUsername(username)) {
            JOptionPane.showMessageDialog(view, "Username must not contain numbers!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(view, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (password.length() <= 6) {
            JOptionPane.showMessageDialog(view, "Password must be greater than 6 characters!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(view, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public static boolean validateForgotPassword(String email, String newPass, String confirm, JFrame view) {
        if (email.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(view, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(view, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (newPass.length() <= 6) {
            JOptionPane.showMessageDialog(view, "Password must be greater than 6 characters!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(view, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private static boolean isValidEmail(String email) {
        return email != null && !email.isEmpty() && email.contains("@") && email.contains(".");
    }
    
    private static boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        for (int i = 0; i < username.length(); i++) {
            if (Character.isDigit(username.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
