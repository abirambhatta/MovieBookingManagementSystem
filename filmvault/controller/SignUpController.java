package filmvault.controller;

import filmvault.view.LoginView;
import filmvault.view.SignUpView;
import filmvault.model.validation;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SignUpController {
    private SignUpView view;

    public SignUpController(SignUpView view) {
        this.view = view;
        initController();
    }

    private void initController() {
        view.getSignUpButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSignUp();
            }
        });

        view.getBackButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                backToLogin();
            }
        });

        view.getBackButton().addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                view.getBackButton().setText("<html><u>Back To Login</u></html>");
            }

            public void mouseExited(MouseEvent evt) {
                view.getBackButton().setText("<html>Back To Login</html>");
            }
        });
    }

    private void performSignUp() {
        String username = view.getUsernameText();
        String email = view.getEmailText();
        String password = view.getPasswordText();
        String confirm = view.getConfirmPasswordText();

        if (!validation.validateSignUp(username, email, password, confirm, view)) {
            return;
        }

        JOptionPane.showMessageDialog(view, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        backToLogin();
    }

    private void backToLogin() {
        view.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }
}
