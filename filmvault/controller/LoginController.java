package filmvault.controller;

import filmvault.view.LoginView;
import filmvault.view.SignUpView;
import filmvault.model.validation;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginController {
    private LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
        initController();
    }

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

        view.getForgotPasswordButton().addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                view.getForgotPasswordButton().setText("<html><u>Forgot Password?</u></html>");
            }

            public void mouseExited(MouseEvent evt) {
                view.getForgotPasswordButton().setText("<html>Forgot Password?</html>");
            }
        });
    }

    private void performLogin() {
        String identifier = view.getEmailText();
        String password = view.getPasswordText();

        if (!validation.validateLogin(identifier, password, view)) {
            return;
        }

        JOptionPane.showMessageDialog(view, "Login Successful!\nWelcome " + identifier, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openSignUp() {
        view.dispose();
        SignUpView signUpView = new SignUpView();
        new SignUpController(signUpView);
        signUpView.setVisible(true);
    }
}
