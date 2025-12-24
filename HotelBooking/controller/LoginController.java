package HotelBooking.controller;

import javax.swing.JOptionPane;

import HotelBooking.model.validation;
import HotelBooking.view.ForgotPasswordView;
import HotelBooking.view.LoginView;
import HotelBooking.view.SignUpView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * LoginController handles login form actions
 * Manages login button, signup button, and forgot password button
 */
public class LoginController {
    private LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
        initController();
    }

    // Initialize button listeners
    private void initController() {
        view.getLoginButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        view.getSignUpButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openSignUp();
            }
        });

        view.getForgotPasswordButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openForgotPassword();
            }
        });

        view.getForgotPasswordButton().addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                view.getForgotPasswordButton().setText("<html><u>Forgot Password?</u></html>");
            }

            public void mouseExited(MouseEvent evt) {
                view.getForgotPasswordButton().setText("<html>Forgot Password?</html>");
            }
        });
    }

    // Validate and perform login
    private void performLogin() {
        String identifier = view.getEmailText();
        String password = view.getPasswordText();

        if (!validation.validateLogin(identifier, password, view)) {
            return;
        }

        JOptionPane.showMessageDialog(view, "Login Successful!\nWelcome " + identifier, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // Open signup form
    private void openSignUp() {
        view.dispose();
        SignUpView signUpView = new SignUpView();
        new SignUpController(signUpView);
        signUpView.setVisible(true);
    }

    // Open forgot password form
    private void openForgotPassword() {
        view.dispose();
        ForgotPasswordView forgotView = new ForgotPasswordView();
        new ForgotPasswordController(forgotView);
        forgotView.setVisible(true);
    }
}
