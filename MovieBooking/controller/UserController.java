package MovieBooking.controller;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import MovieBooking.model.User;
import MovieBooking.view.MovieBookingView;
import MovieBooking.view.AuthenticationView;

public class UserController {
    private MovieBookingView view;
    private javax.swing.JButton activeButton;
    private String loggedInUserIdentifier;

    public UserController(MovieBookingView view, String loggedInUserIdentifier) {
        this.view = view;
        this.loggedInUserIdentifier = loggedInUserIdentifier;
        java.awt.CardLayout cl = (java.awt.CardLayout) view.getContentPane().getLayout();
        cl.show(view.getContentPane(), "card3");

        initUserController();
        updateWelcomeBar();
        showUserHome();
    }

    private void initUserController() {
        javax.swing.JButton[][] buttonSets = {
            {view.getHomeButton1(), view.getMoviesButton1(), view.getUsersButton1(), view.getUsersButton2(), view.getLogoutButton1()},
            {view.getHomeButton2(), view.getMoviesButton2(), view.getUsersButton3(), view.getUsersButton4(), view.getLogoutButton2()},
            {view.getHomeButton3(), view.getMoviesButton3(), view.getUsersButton5(), view.getUsersButton6(), view.getLogoutButton3()},
            {view.getHomeButton4(), view.getMoviesButton4(), view.getUsersButton7(), view.getUsersButton8(), view.getLogoutButton4()}
        };
        for (javax.swing.JButton[] buttons : buttonSets) {
            setupPageButtons(buttons[0], buttons[1], buttons[2], buttons[3], buttons[4]);
        }
    }

    private void setupPageButtons(javax.swing.JButton homeBtn, javax.swing.JButton moviesBtn, javax.swing.JButton bookingBtn, javax.swing.JButton profileBtn, javax.swing.JButton logoutBtn) {
        homeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showUserHome();
            }
        });
        addButtonHoverListeners(homeBtn);

        moviesBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showBrowseMovies();
            }
        });
        addButtonHoverListeners(moviesBtn);

        bookingBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMyBooking();
            }
        });
        addButtonHoverListeners(bookingBtn);

        profileBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showProfile();
            }
        });
        addButtonHoverListeners(profileBtn);

        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performLogout();
            }
        });
    }

    private void addButtonHoverListeners(javax.swing.JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != activeButton) {
                    button.setOpaque(true);
                    button.setBackground(new java.awt.Color(255, 200, 200));
                    button.setForeground(new java.awt.Color(229, 9, 20));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != activeButton) {
                    button.setOpaque(false);
                    button.setForeground(new java.awt.Color(0, 0, 0));
                    button.setBackground(new java.awt.Color(255, 255, 255));
                }
            }
        });
    }

    private void showUserHome() {
        showCard("card2", view.getHomeButton1(), view.getHomeButton2(), view.getHomeButton3(), view.getHomeButton4());
    }

    private void showBrowseMovies() {
        showCard("card3", view.getMoviesButton1(), view.getMoviesButton2(), view.getMoviesButton3(), view.getMoviesButton4());
    }

    private void showMyBooking() {
        showCard("card4", view.getUsersButton1(), view.getUsersButton3(), view.getUsersButton5(), view.getUsersButton7());
    }

    private void showProfile() {
        showCard("card5", view.getUsersButton2(), view.getUsersButton4(), view.getUsersButton6(), view.getUsersButton8());
    }
    
    private void showCard(String cardName, javax.swing.JButton... buttons) {
        java.awt.CardLayout cl = (java.awt.CardLayout) view.getUserPanel().getLayout();
        cl.show(view.getUserPanel(), cardName);
        resetAllButtons();
        setActiveButtons(buttons);
    }

    private void resetAllButtons() {
        javax.swing.JButton[] allButtons = {
            view.getHomeButton1(), view.getMoviesButton1(), view.getUsersButton1(), view.getUsersButton2(),
            view.getHomeButton2(), view.getMoviesButton2(), view.getUsersButton3(), view.getUsersButton4(),
            view.getHomeButton3(), view.getMoviesButton3(), view.getUsersButton5(), view.getUsersButton6(),
            view.getHomeButton4(), view.getMoviesButton4(), view.getUsersButton7(), view.getUsersButton8()
        };
        for (javax.swing.JButton btn : allButtons) {
            btn.setOpaque(false);
            btn.setForeground(new java.awt.Color(0, 0, 0));
            btn.setBackground(new java.awt.Color(255, 255, 255));
        }
    }

    private void setActiveButtons(javax.swing.JButton... buttons) {
        for (javax.swing.JButton btn : buttons) {
            btn.setOpaque(true);
            btn.setBackground(new java.awt.Color(229, 9, 20));
            btn.setForeground(new java.awt.Color(255, 255, 255));
        }
        activeButton = buttons[0];
    }

    private void updateWelcomeBar() {
        // Get user details
        User user = User.getUserDetails(loggedInUserIdentifier);
        
        if (user != null) {
            // Update home page welcome bar
            view.getWelcomeLabel().setText("Welcome " + user.getUsername());
            view.getEmailLabel().setText(user.getEmail());
            
            // Update today's date
            LocalDate today = LocalDate.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            view.getDateLabel().setText("Today's Date: " + today.format(dateFormatter));
            
            // Update member since date
            if (user.getRegistrationDate() != null) {
                view.getMemberSinceLabel().setText("Member Since: " + user.getRegistrationDate().format(dateFormatter));
            } else {
                view.getMemberSinceLabel().setText("Member Since: " + today.format(dateFormatter));
            }
            
            // Update profile page welcome bar (if it exists)
            view.getProfileWelcomeLabel().setText("Welcome " + user.getUsername());
            view.getProfileEmailLabel().setText(user.getEmail());
            view.getProfileDateLabel().setText("Today's Date: " + today.format(dateFormatter));
            if (user.getRegistrationDate() != null) {
                view.getProfileMemberSinceLabel().setText("Member Since: " + user.getRegistrationDate().format(dateFormatter));
            } else {
                view.getProfileMemberSinceLabel().setText("Member Since: " + today.format(dateFormatter));
            }
        }
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            view.dispose();
            AuthenticationView authView = new AuthenticationView();
            new AuthenticationController(authView);
            authView.setVisible(true);
        }
    }
}
