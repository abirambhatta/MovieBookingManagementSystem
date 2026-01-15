
package MovieBooking.controller;

import javax.swing.JOptionPane;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import MovieBooking.model.validation;
import MovieBooking.model.Admin;
import MovieBooking.model.User;
import MovieBooking.view.AuthenticationView;
import MovieBooking.view.MovieBookingView;
import MovieBooking.controller.AdminController;
import MovieBooking.controller.UserController;

/**
 * AuthenticationController manages the user entry point of the application.
 * It coordinates with AuthenticationView and uses CardLayout to toggle between
 * Login, Sign-Up, and Forgot Password screens.
 */
public class AuthenticationController {
    /** The view being managed by this controller */
    private AuthenticationView view;

    /** Layout manager for switching between auth cards */
    private CardLayout cardLayout;

    /** Card identifiers for CardLayout internal switching */
    private static final String LOGIN_CARD = "card2";
    private static final String SIGNUP_CARD = "card3";
    private static final String FORGOT_CARD = "card4";

    /**
     * Constructs the controller and initializes the starting state.
     * 
     * @param view The login/registration frame.
     */
    public AuthenticationController(AuthenticationView view) {
        this.view = view;
        this.cardLayout = (CardLayout) view.getContentPane().getLayout();
        initController();
        showLoginCard(); // Application starts here
    }

    private void initController() {
        // Login card buttons
        view.getLoginButton4().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        view.getSignUpButton4().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSignUpCard();
            }
        });

        view.getForgotPasswordButton4().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showForgotCard();
            }
        });
        view.getForgotPasswordButton4().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                view.getForgotPasswordButton4().setText("<html><u>Forgot Password?</u></html>");
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                view.getForgotPasswordButton4().setText("Forgot Password?");
            }
        });

        // SignUp card buttons
        view.getSignupbutton1().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSignUp();
            }
        });

        view.getBackToLoginButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLoginCard();
            }
        });
        view.getBackToLoginButton().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                view.getBackToLoginButton().setText("<html><u>Back To Login</u></html>");
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                view.getBackToLoginButton().setText("Back To Login");
            }
        });

        // Forgot password card buttons
        view.getResetPasswordButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performForgotPassword();
            }
        });

        view.getBackToLoginButton1().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLoginCard();
            }
        });
        view.getBackToLoginButton1().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                view.getBackToLoginButton1().setText("<html><u>Back To Login</u></html>");
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                view.getBackToLoginButton1().setText("Back To Login");
            }
        });

        setupEnterKeyNavigation();
    }

    /**
     * Switches the view to the Login card and resets fields.
     */
    private void showLoginCard() {
        cardLayout.show(view.getContentPane(), LOGIN_CARD);
        // Pack to shrink/expand window based on card contents
        if (view.getExtendedState() != javax.swing.JFrame.MAXIMIZED_BOTH) {
            view.pack();
            view.setLocationRelativeTo(null);
        }
        view.revalidate();
        view.repaint();
        clearFields();
    }

    /**
     * Switches the view to the Sign-Up card.
     */
    private void showSignUpCard() {
        cardLayout.show(view.getContentPane(), SIGNUP_CARD);
        if (view.getExtendedState() != javax.swing.JFrame.MAXIMIZED_BOTH) {
            view.pack();
            view.setLocationRelativeTo(null);
        }
        clearFields();
    }

    /**
     * Switches the view to the Forgot Password card.
     */
    private void showForgotCard() {
        cardLayout.show(view.getContentPane(), FORGOT_CARD);
        if (view.getExtendedState() != javax.swing.JFrame.MAXIMIZED_BOTH) {
            view.pack();
            view.setLocationRelativeTo(null);
        }
        view.revalidate();
        view.repaint();
        clearFields();
    }

    /**
     * Extracts credentials and validates them against Admin and User records.
     */
    private void performLogin() {
        String identifier = view.getEmailTextField4().getText().trim();
        String password = new String(view.getPasswordPasswordField4().getPassword());

        // Basic format validation
        if (!validation.validateLogin(identifier, password, view)) {
            return;
        }

        // 1. Check if it's an Administrative login
        if (Admin.isAdmin(identifier, password)) {
            JOptionPane.showMessageDialog(view, "Admin Login Successful!\nWelcome Administrator", "Admin Access",
                    JOptionPane.INFORMATION_MESSAGE);
            view.dispose();
            MovieBookingView movieBookingView = new MovieBookingView();
            new AdminController(movieBookingView);
            movieBookingView.setVisible(true);
        }
        // 2. Check regular User login
        else if (User.authenticateUser(identifier, password)) {
            // Enforcement: block users marked as 'Blocked' by admin
            if (User.isUserBlocked(identifier)) {
                JOptionPane.showMessageDialog(view, "You have been blocked by the admin!", "Access Denied",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(view, "User Login Successful!\nWelcome " + identifier, "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            view.dispose();
            MovieBookingView movieBookingView = new MovieBookingView();
            new UserController(movieBookingView, identifier);
            movieBookingView.setVisible(true);
        }
        // 3. Fallback for failed authentication
        else {
            if (User.isUserBlocked(identifier)) {
                JOptionPane.showMessageDialog(view, "You have been blocked by the admin!", "Access Denied",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Invalid credentials!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Logic for registering a new user account.
     */
    private void performSignUp() {
        String username = view.getUsernameTextField().getText().trim();
        String email = view.getEmailTextField5().getText().trim();
        String password = new String(view.getPasswordPasswordField5().getPassword());
        String confirm = new String(view.getConfirmPasswordPasswordField().getPassword());

        // Validate form requirements
        if (!validation.validateSignUp(username, email, password, confirm, view)) {
            return;
        }

        // Prevent duplicate registration
        if (User.userExists(username, email)) {
            JOptionPane.showMessageDialog(view, "Username or Email already exists!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Persist new user
        if (User.saveUser(username, email, password)) {
            JOptionPane.showMessageDialog(view, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            showLoginCard();
        } else {
            JOptionPane.showMessageDialog(view, "Registration failed! Please try again.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Resets the user's password if the identity email exists in the records.
     */
    private void performForgotPassword() {
        String email = view.getEmailTextField6().getText().trim();
        String newPass = new String(view.getNewPasswordPasswordField().getPassword());
        String confirm = new String(view.getConfirmPasswordPasswordField1().getPassword());

        if (!validation.validateForgotPassword(email, newPass, confirm, view)) {
            return;
        }

        // Update database file
        if (User.updatePassword(email, newPass)) {
            JOptionPane.showMessageDialog(view, "Password reset successful!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            showLoginCard();
        } else {
            JOptionPane.showMessageDialog(view, "Email not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Resets all input fields in the authentication frame.
     */
    private void clearFields() {
        // Clear login fields
        view.getEmailTextField4().setText("");
        view.getPasswordPasswordField4().setText("");

        // Clear signup fields
        view.getUsernameTextField().setText("");
        view.getEmailTextField5().setText("");
        view.getPasswordPasswordField5().setText("");
        view.getConfirmPasswordPasswordField().setText("");

        // Clear forgot password fields
        view.getEmailTextField6().setText("");
        view.getNewPasswordPasswordField().setText("");
        view.getConfirmPasswordPasswordField1().setText("");
    }

    /**
     * Sets up Enter key listeners for smooth form navigation and submission.
     */
    private void setupEnterKeyNavigation() {
        // Login flow navigation
        view.getEmailTextField4().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    view.getPasswordPasswordField4().requestFocus();
                }
            }
        });

        view.getPasswordPasswordField4().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        // SignUp flow navigation
        view.getUsernameTextField().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    view.getEmailTextField5().requestFocus();
                }
            }
        });

        view.getEmailTextField5().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    view.getPasswordPasswordField5().requestFocus();
                }
            }
        });

        view.getPasswordPasswordField5().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    view.getConfirmPasswordPasswordField().requestFocus();
                }
            }
        });

        view.getConfirmPasswordPasswordField().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    performSignUp();
                }
            }
        });

        // Forgot Password flow navigation
        view.getEmailTextField6().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    view.getNewPasswordPasswordField().requestFocus();
                }
            }
        });

        view.getNewPasswordPasswordField().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    view.getConfirmPasswordPasswordField1().requestFocus();
                }
            }
        });

        view.getConfirmPasswordPasswordField1().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    performForgotPassword();
                }
            }
        });
    }
}