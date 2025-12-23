package filmvault.controller;

import filmvault.view.ForgotPasswordView;
import filmvault.view.LoginView;
import filmvault.model.validation;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ForgotPasswordController {
    private ForgotPasswordView view;

    public ForgotPasswordController(ForgotPasswordView view) {
        this.view = view;
        initController();
    }

    private void initController() {
        view.getResetButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performReset();
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

    private void performReset() {
        String email = view.getEmailText();
        String newPass = view.getNewPasswordText();
        String confirm = view.getConfirmPasswordText();

        if (!validation.validateForgotPassword(email, newPass, confirm, view)) {
            return;
        }

        JOptionPane.showMessageDialog(view, "Password reset successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        backToLogin();
    }

    private void backToLogin() {
        view.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }
}
