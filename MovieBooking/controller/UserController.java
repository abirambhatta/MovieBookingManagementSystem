package MovieBooking.controller;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import MovieBooking.model.User;
import MovieBooking.model.Movie;
import MovieBooking.model.Ticket;
import MovieBooking.view.MovieBookingView;
import MovieBooking.view.AuthenticationView;
import MovieBooking.controller.AuthenticationController;

/**
 * UserController manages the user interface and logic for customers.
 * It handles movie browsing, booking, profile management, and navigation.
 */
public class UserController {
    /** The main application view */
    private MovieBookingView view;

    /** Set of currently highlighted sidebar buttons */
    private Set<javax.swing.JButton> activeButtons;

    /** Email of the currently logged-in user */
    private String loggedInUserIdentifier;

    /** Cache of movies loaded from file */
    private ArrayList<Movie> movieList;

    /** Dynamically generated panel for the movie gallery */
    private JPanel dynamicGalleryPanel;

    /** Constant for movie data file path */
    private static final String MOVIE_FILE = "src/MovieBooking/movies.txt";

    /** State variables for current booking process */
    private Movie currentMovie;
    private Set<javax.swing.JToggleButton> selectedSeats;
    private javax.swing.JToggleButton selectedTimeBtn;
    private String selectedDate;

    /**
     * Initializes a new UserController for the specified user.
     * 
     * @param view                   The shared application view instance.
     * @param loggedInUserIdentifier The email/username of the user.
     */
    public UserController(MovieBookingView view, String loggedInUserIdentifier) {
        this.view = view;
        this.loggedInUserIdentifier = loggedInUserIdentifier;
        this.activeButtons = new HashSet<>();
        this.selectedSeats = new HashSet<>();
        this.movieList = new ArrayList<>();

        // Show the user dashboard by default
        java.awt.CardLayout cl = (java.awt.CardLayout) view.getContentPane().getLayout();
        cl.show(view.getContentPane(), "card3");

        initBrowsePageLayout();
        initUserController();
        initBookingListeners();
        updateWelcomeBar();
        configureTables();
        loadMoviesFromFile();
        populateFilters(true); // Default to "all genre/language"
        showUserHome();
        refreshBookingTables();
        updateUserDashboard();
    }

    /**
     * Maps listeners to all navigation and action buttons.
     */
    private void initUserController() {
        // Button sets across different pages for consistent navigation
        javax.swing.JButton[][] buttonSets = {
                { view.getHomeButton1(), view.getMoviesButton1(), view.getUsersButton1(), view.getUsersButton2(),
                        view.getLogoutButton1() },
                { view.getHomeButton2(), view.getMoviesButton2(), view.getUsersButton3(), view.getUsersButton4(),
                        view.getLogoutButton2() },
                { view.getHomeButton3(), view.getMoviesButton3(), view.getUsersButton5(), view.getUsersButton6(),
                        view.getLogoutButton3() },
                { view.getHomeButton4(), view.getMoviesButton4(), view.getUsersButton7(), view.getUsersButton8(),
                        view.getLogoutButton4() }
        };
        for (javax.swing.JButton[] buttons : buttonSets) {
            setupPageButtons(buttons[0], buttons[1], buttons[2], buttons[3], buttons[4]);
        }
        initSearchAndFilters();
        initMyBookingListeners();
        initProfileButtons();
    }

    private void initProfileButtons() {
        view.getSignupbutton1().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleUpdateProfile();
            }
        });
        view.getSignupbutton2().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleDeleteAccount();
            }
        });
        view.getCloseTicketButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshBookingTables();
            }
        });
        view.getViewTicketButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleViewTicket();
            }
        });
    }

    /**
     * Configures listeners and hover effects for a set of sidebar buttons.
     */
    private void setupPageButtons(javax.swing.JButton homeBtn, javax.swing.JButton moviesBtn,
            javax.swing.JButton bookingBtn, javax.swing.JButton profileBtn, javax.swing.JButton logoutBtn) {
        homeBtn.addActionListener(e -> showUserHome());
        addButtonHoverListeners(homeBtn);

        moviesBtn.addActionListener(e -> showBrowseMovies());
        addButtonHoverListeners(moviesBtn);

        bookingBtn.addActionListener(e -> showMyBooking());
        addButtonHoverListeners(bookingBtn);

        profileBtn.addActionListener(e -> showProfile());
        addButtonHoverListeners(profileBtn);

        logoutBtn.addActionListener(e -> performLogout());
    }

    /**
     * Adds visual hover effects to buttons if they are not active.
     */
    private void addButtonHoverListeners(javax.swing.JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!activeButtons.contains(button)) {
                    button.setOpaque(true);
                    button.setBackground(new java.awt.Color(255, 200, 200));
                    button.setForeground(new java.awt.Color(229, 9, 20));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!activeButtons.contains(button)) {
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
        showCard("card3", view.getMoviesButton1(), view.getMoviesButton2(), view.getMoviesButton3(),
                view.getMoviesButton4());
        populateMoviePanels();
    }

    private void showMyBooking() {
        showCard("card4", view.getUsersButton1(), view.getUsersButton3(), view.getUsersButton5(),
                view.getUsersButton7());
    }

    private void showProfile() {
        User user = User.getUserDetails(loggedInUserIdentifier);
        if (user != null) {
            view.getUsernameTextField().setText(user.getUsername());
            view.getEmailTextField6().setText(user.getEmail());
            view.getPasswordTextField1().setText(user.getPassword());
        }

        showCard("card5", view.getUsersButton2(), view.getUsersButton4(), view.getUsersButton6(),
                view.getUsersButton8());
    }

    private void handleUpdateProfile() {
        String newUsername = view.getUsernameTextField().getText().trim();
        String newEmail = view.getEmailTextField6().getText().trim();
        String newPassword = view.getPasswordTextField1().getText().trim();

        if (!MovieBooking.model.validation.validateProfileUpdate(newUsername, newEmail, newPassword, view)) {
            return;
        }

        User user = User.getUserDetails(loggedInUserIdentifier);
        if (user == null) {
            JOptionPane.showMessageDialog(view, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Are you sure you want to update your profile?",
                "Update Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (User.updateUser(user.getEmail(), newUsername, newEmail, newPassword)) {
                JOptionPane.showMessageDialog(view, "Profile updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loggedInUserIdentifier = newEmail; // Update identifier in case email changed
                updateWelcomeBar();
                showProfile();
            } else {
                JOptionPane.showMessageDialog(view, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeleteAccount() {
        int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete your account? This action cannot be undone.",
                "Delete Account Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String password = JOptionPane.showInputDialog(view, "Please enter your password to confirm deletion:");
            if (password == null)
                return;

            User user = User.getUserDetails(loggedInUserIdentifier);
            if (user != null && user.getPassword().equals(password)) {
                if (User.deleteUser(user.getEmail())) {
                    JOptionPane.showMessageDialog(view, "Account deleted successfully.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    view.dispose();
                    AuthenticationView authView = new AuthenticationView();
                    new AuthenticationController(authView);
                    authView.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(view, "Failed to delete account.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(view, "Incorrect password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Helper to switch cards and update active button highlighting.
     * 
     * @param cardName The identifier of the card to show.
     * @param buttons  The buttons corresponding to this card.
     */
    private void showCard(String cardName, javax.swing.JButton... buttons) {
        java.awt.CardLayout cl = (java.awt.CardLayout) view.getUserPanel().getLayout();
        cl.show(view.getUserPanel(), cardName);
        resetAllButtons();
        setActiveButtons(buttons);
    }

    /**
     * Resets all navigation buttons to their default (inactive) appearance.
     */
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

    /**
     * Highlights the buttons corresponding to the current active card.
     */
    private void setActiveButtons(javax.swing.JButton... buttons) {
        activeButtons.clear();
        for (javax.swing.JButton btn : buttons) {
            btn.setOpaque(true);
            btn.setBackground(new java.awt.Color(229, 9, 20));
            btn.setForeground(new java.awt.Color(255, 255, 255));
            activeButtons.add(btn);
        }
    }

    /**
     * Updates the welcome message, email, and dates on the user home dashboard.
     */
    private void updateWelcomeBar() {
        User user = User.getUserDetails(loggedInUserIdentifier);

        if (user != null) {
            view.getWelcomeLabel().setText("Welcome " + user.getUsername());
            view.getEmailLabel().setText(user.getEmail());

            // Format dates for display
            LocalDate today = LocalDate.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            view.getDateLabel().setText("Today's Date: " + today.format(dateFormatter));

            if (user.getRegistrationDate() != null) {
                view.getMemberSinceLabel().setText("Member Since: " + user.getRegistrationDate().format(dateFormatter));
            } else {
                view.getMemberSinceLabel().setText("Member Since: " + today.format(dateFormatter));
            }
        }
    }

    /**
     * Terminates the user session and returns to login.
     */
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

    /**
     * Loads the movie database from file into memory.
     */
    private void loadMoviesFromFile() {
        movieList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(MOVIE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String imagePath = parts.length > 6 ? parts[6] : "";
                    movieList.add(new Movie(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], imagePath));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view, "Error loading movies from file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateMoviePanels() {
        loadMoviesFromFile();
        populateFilters(false); // Restore selection if navigating
        displayMovies(movieList);
    }

    private void displayMovies(ArrayList<Movie> movies) {
        if (dynamicGalleryPanel == null)
            return;

        dynamicGalleryPanel.removeAll();

        for (Movie movie : movies) {
            dynamicGalleryPanel.add(createMoviePanel(movie));
        }

        dynamicGalleryPanel.revalidate();
        dynamicGalleryPanel.repaint();
    }

    /**
     * Initializes the Browse Movies page layout.
     * This method dynamically restructures the view's content panel to implement
     * a clean, scrollable gallery view with headers and filters.
     */
    private void initBrowsePageLayout() {
        JPanel mainPanel = view.getMoviePanelContainer();

        // Extract and keep references to existing UI components for reuse in new layout
        java.awt.Component[] components = mainPanel.getComponents();
        JLabel browseTitle = null;
        javax.swing.JTextField sBar = view.getSearchBar();
        JButton sBtn = view.getSearchButton();
        JLabel filterLabel1 = null; // genre label
        JLabel filterLabel2 = null; // language label
        javax.swing.JComboBox<String> fGenre = view.getFilterGenre();
        javax.swing.JComboBox<String> fLang = view.getFilterLanguage();
        JLabel recentLabel = null;

        for (java.awt.Component comp : components) {
            if (comp instanceof JLabel) {
                JLabel lbl = (JLabel) comp;
                String text = lbl.getText() != null ? lbl.getText() : "";
                if (text.contains("Browse Movie"))
                    browseTitle = lbl;
                else if (text.contains("Genre:"))
                    filterLabel1 = lbl;
                else if (text.contains("Language:"))
                    filterLabel2 = lbl;
                else if (text.contains("Now Showing"))
                    recentLabel = lbl;
            }
        }

        if (recentLabel == null) {
            for (java.awt.Component comp : components) {
                if (comp.getName() != null && comp.getName().equals("RecentBooking4")) {
                    recentLabel = (JLabel) comp;
                    break;
                }
            }
        }

        // Reset main panel layout to BorderLayout
        mainPanel.removeAll();
        mainPanel.setLayout(new java.awt.BorderLayout(0, 0));

        // Construct the Header Panel (Title, Search, Filters)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new java.awt.Color(249, 249, 249));
        headerPanel.setLayout(new javax.swing.BoxLayout(headerPanel, javax.swing.BoxLayout.Y_AXIS));
        headerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 26, 10, 0));

        // 1. Title Row
        if (browseTitle == null) {
            browseTitle = new JLabel("Browse Movies");
            browseTitle.setFont(new java.awt.Font("Segoe UI", 1, 24));
        }
        browseTitle.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        headerPanel.add(browseTitle);
        headerPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 15)));

        // 2. Search Row
        JPanel searchRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        searchRow.setOpaque(false);
        sBar.setPreferredSize(new java.awt.Dimension(300, 32));
        searchRow.add(sBar);
        searchRow.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(18, 0)));
        searchRow.add(sBtn);
        searchRow.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        headerPanel.add(searchRow);
        headerPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 15)));

        // 3. Status/Section Label
        if (recentLabel == null) {
            recentLabel = new JLabel("Now Showing");
            recentLabel.setFont(new java.awt.Font("Segoe UI", 1, 18));
        } else {
            recentLabel.setText("Now Showing");
        }
        recentLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        headerPanel.add(recentLabel);
        headerPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));

        // 4. Filter Row
        JPanel filterRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        filterRow.setOpaque(false);
        if (filterLabel1 == null)
            filterLabel1 = new JLabel("genre: ");
        else
            filterLabel1.setText("genre: ");

        if (filterLabel2 == null)
            filterLabel2 = new JLabel("language: ");
        else
            filterLabel2.setText("language: ");

        filterRow.add(filterLabel1);
        filterRow.add(fGenre);
        filterRow.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(18, 0)));
        filterRow.add(filterLabel2);
        filterRow.add(fLang);
        filterRow.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        headerPanel.add(filterRow);
        headerPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 20)));

        // 5. Gallery Panel Initialization
        dynamicGalleryPanel = new JPanel();
        dynamicGalleryPanel.setBackground(new java.awt.Color(249, 249, 249));
        dynamicGalleryPanel.setLayout(new java.awt.GridLayout(0, 2, 30, 30)); // 2 Columns
        dynamicGalleryPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 26, 0, 26));

        // Finalize assembly
        mainPanel.add(headerPanel, java.awt.BorderLayout.NORTH);
        mainPanel.add(dynamicGalleryPanel, java.awt.BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private boolean isPopulatingFilters = false;

    /**
     * Populates the genre and language filter dropdowns based on available movies.
     * 
     * @param forceAll If true, resets selection to "all".
     */
    private void populateFilters(boolean forceAll) {
        isPopulatingFilters = true;

        String currentGenre = null;
        String currentLang = null;

        // Save current selection to restore after refresh
        if (!forceAll) {
            currentGenre = (String) view.getFilterGenre().getSelectedItem();
            currentLang = (String) view.getFilterLanguage().getSelectedItem();
        }

        view.getFilterGenre().removeAllItems();
        view.getFilterLanguage().removeAllItems();

        view.getFilterGenre().addItem("all genre");
        view.getFilterLanguage().addItem("all language");

        // Use TreeSet for automatic sorting and uniqueness
        TreeSet<String> genres = new TreeSet<>();
        TreeSet<String> languages = new TreeSet<>();

        for (Movie m : movieList) {
            if (m.getGenre() != null && !m.getGenre().isEmpty()) {
                genres.add(m.getGenre());
            }
            if (m.getLanguage() != null && !m.getLanguage().isEmpty()) {
                languages.add(m.getLanguage());
            }
        }

        for (String g : genres)
            view.getFilterGenre().addItem(g);
        for (String l : languages)
            view.getFilterLanguage().addItem(l);

        // Restore or default selection
        if (currentGenre != null && !forceAll)
            view.getFilterGenre().setSelectedItem(currentGenre);
        else
            view.getFilterGenre().setSelectedItem("all genre");

        if (currentLang != null && !forceAll)
            view.getFilterLanguage().setSelectedItem(currentLang);
        else
            view.getFilterLanguage().setSelectedItem("all language");

        isPopulatingFilters = false;
    }

    /**
     * Hooks up listeners for search and filter controls in the browse page.
     */
    private void initSearchAndFilters() {
        // Search button click starts filtering
        view.getSearchButton().addActionListener(e -> handleFiltering());

        // Dropdown selection triggers filtering immediately
        view.getFilterGenre().addActionListener(e -> {
            if (!isPopulatingFilters)
                handleFiltering();
        });

        view.getFilterLanguage().addActionListener(e -> {
            if (!isPopulatingFilters)
                handleFiltering();
        });
    }

    /**
     * Applies search text and dropdown filters to the movie list and refreshes the
     * display.
     */
    private void handleFiltering() {
        String searchText = view.getSearchBar().getText().toLowerCase();
        String selectedGenre = (String) view.getFilterGenre().getSelectedItem();
        String selectedLang = (String) view.getFilterLanguage().getSelectedItem();

        ArrayList<Movie> filteredList = new ArrayList<>();
        for (Movie movie : movieList) {
            boolean matchesSearch = movie.getName().toLowerCase().contains(searchText);
            boolean matchesGenre = selectedGenre == null || selectedGenre.equals("all genre")
                    || movie.getGenre().equals(selectedGenre);
            boolean matchesLang = selectedLang == null || selectedLang.equals("all language")
                    || movie.getLanguage().equals(selectedLang);

            if (matchesSearch && matchesGenre && matchesLang) {
                filteredList.add(movie);
            }
        }
        displayMovies(filteredList);
    }

    /**
     * Dynamically creates a UI panel for a single movie card.
     * 
     * @param movie The movie data to display.
     * @return A JPanel containing the movie poster, details, and "Book Now" button.
     */
    private javax.swing.JPanel createMoviePanel(Movie movie) {
        // Theme Colors
        java.awt.Color themeBackground = new java.awt.Color(249, 249, 249);
        java.awt.Color themeRed = new java.awt.Color(229, 9, 20);

        // 1. Outer Container
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setBackground(themeBackground);
        panel.setBorder(javax.swing.BorderFactory.createLineBorder(themeRed));
        panel.setLayout(new java.awt.BorderLayout());
        panel.setPreferredSize(new java.awt.Dimension(250, 420));

        // 2. Poster Section (Top)
        javax.swing.JPanel posterPanel = new javax.swing.JPanel();
        posterPanel.setBackground(java.awt.Color.WHITE);
        posterPanel.setPreferredSize(new java.awt.Dimension(250, 201));
        posterPanel.setLayout(new java.awt.BorderLayout());
        posterPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, themeRed));

        if (movie.getImagePath() != null && !movie.getImagePath().isEmpty()) {
            try {
                // Load and scale poster image
                javax.swing.ImageIcon icon = new javax.swing.ImageIcon(movie.getImagePath());
                java.awt.Image img = icon.getImage();
                java.awt.Image scaledImg = img.getScaledInstance(250, 201, java.awt.Image.SCALE_SMOOTH);
                posterPanel.add(new javax.swing.JLabel(new javax.swing.ImageIcon(scaledImg)),
                        java.awt.BorderLayout.CENTER);
            } catch (Exception e) {
                posterPanel.add(new javax.swing.JLabel("No Image", javax.swing.SwingConstants.CENTER));
            }
        } else {
            posterPanel.add(new javax.swing.JLabel("No Image", javax.swing.SwingConstants.CENTER));
        }

        // 3. Detail Section (Center)
        javax.swing.JPanel detailPanel = new javax.swing.JPanel();
        detailPanel.setBackground(new java.awt.Color(255, 245, 245));
        detailPanel.setLayout(new javax.swing.BoxLayout(detailPanel, javax.swing.BoxLayout.Y_AXIS));
        detailPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 19, 10, 19));

        javax.swing.JLabel nameLabel = new javax.swing.JLabel(movie.getName());
        nameLabel.setForeground(themeRed);
        nameLabel.setFont(new java.awt.Font("Segoe UI", 1, 14));

        javax.swing.JLabel genreLabel = new javax.swing.JLabel("Genre: " + movie.getGenre());
        genreLabel.setForeground(java.awt.Color.DARK_GRAY);

        javax.swing.JLabel timeLabel = new javax.swing.JLabel("Time: " + movie.getDuration());
        timeLabel.setForeground(java.awt.Color.DARK_GRAY);

        javax.swing.JLabel langLabel = new javax.swing.JLabel("Language: " + movie.getLanguage());
        langLabel.setForeground(java.awt.Color.DARK_GRAY);

        // "Book Now" Button and its centered container
        javax.swing.JButton bookBtn = new javax.swing.JButton("Book Now");
        bookBtn.setBackground(themeRed);
        bookBtn.setForeground(java.awt.Color.WHITE);
        bookBtn.setFont(new java.awt.Font("Segoe UI", 1, 12));
        bookBtn.setPreferredSize(new java.awt.Dimension(100, 30));
        bookBtn.setFocusPainted(false);
        bookBtn.addActionListener(e -> {
            this.currentMovie = movie;
            showBookingDialog();
        });

        javax.swing.JPanel buttonContainer = new javax.swing.JPanel(
                new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        buttonContainer.setOpaque(false);
        buttonContainer.add(bookBtn);

        // Assembly detail components with spacing
        detailPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 5)));
        detailPanel.add(nameLabel);
        detailPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 8)));
        detailPanel.add(genreLabel);
        detailPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 3)));
        detailPanel.add(timeLabel);
        detailPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 3)));
        detailPanel.add(langLabel);
        detailPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 20)));
        detailPanel.add(buttonContainer);

        panel.add(posterPanel, java.awt.BorderLayout.NORTH);
        panel.add(detailPanel, java.awt.BorderLayout.CENTER);

        return panel;
    }

    /**
     * Resets state and opens the movie booking dialog.
     */
    private void showBookingDialog() {
        selectedSeats.clear();
        selectedTimeBtn = null;
        resetBookingUI();
        view.getPriceLabel().setText("0");

        // Ensure card2 (Selection) is showing initially
        java.awt.CardLayout cl = (java.awt.CardLayout) view.getBookingDialog().getContentPane().getLayout();
        cl.show(view.getBookingDialog().getContentPane(), "card2");

        view.getBookingDialog().pack();
        view.getBookingDialog().setLocationRelativeTo(view);
        view.getMovieNameLabel().setText(currentMovie.getName());
        view.getBookingDialog().setVisible(true);
    }

    private void resetBookingUI() {
        javax.swing.JToggleButton[] allSeats = {
                view.getSeatA1(), view.getSeatA2(), view.getSeatA3(), view.getSeatA4(), view.getSeatA5(),
                view.getSeatA6(),
                view.getSeatA7(), view.getSeatA8(),
                view.getSeatB1(), view.getSeatB2(), view.getSeatB3(), view.getSeatB4(), view.getSeatB5(),
                view.getSeatB6(),
                view.getSeatB7(), view.getSeatB8(),
                view.getSeatC1(), view.getSeatC2(), view.getSeatC3(), view.getSeatC4(), view.getSeatC5(),
                view.getSeatC6(),
                view.getSeatC7(), view.getSeatC8(),
                view.getSeatD1(), view.getSeatD2(), view.getSeatD3(), view.getSeatD4(), view.getSeatD5(),
                view.getSeatD6(),
                view.getSeatD7(), view.getSeatD8()
        };
        for (javax.swing.JToggleButton seat : allSeats) {
            seat.setSelected(false);
            seat.setBackground(null);
        }
        javax.swing.JToggleButton[] timeBtns = { view.getTimeBtn1(), view.getTimeBtn2(), view.getTimeBtn3(),
                view.getTimeBtn4() };
        for (javax.swing.JToggleButton btn : timeBtns) {
            btn.setSelected(false);
            btn.setBackground(null);
        }
        view.getTodayButton().setSelected(false);
        view.getTodayButton().setBackground(null);
        view.getTomorrowButton().setSelected(false);
        view.getTomorrowButton().setBackground(null);
        selectedDate = null;
    }

    /**
     * Hooks up listeners for all interactive components within the booking dialog.
     */
    private void initBookingListeners() {
        // 1. Seat Button Listeners
        javax.swing.JToggleButton[] allSeats = {
                view.getSeatA1(), view.getSeatA2(), view.getSeatA3(), view.getSeatA4(), view.getSeatA5(),
                view.getSeatA6(),
                view.getSeatA7(), view.getSeatA8(),
                view.getSeatB1(), view.getSeatB2(), view.getSeatB3(), view.getSeatB4(), view.getSeatB5(),
                view.getSeatB6(),
                view.getSeatB7(), view.getSeatB8(),
                view.getSeatC1(), view.getSeatC2(), view.getSeatC3(), view.getSeatC4(), view.getSeatC5(),
                view.getSeatC6(),
                view.getSeatC7(), view.getSeatC8(),
                view.getSeatD1(), view.getSeatD2(), view.getSeatD3(), view.getSeatD4(), view.getSeatD5(),
                view.getSeatD6(),
                view.getSeatD7(), view.getSeatD8()
        };
        for (javax.swing.JToggleButton seat : allSeats) {
            seat.addActionListener(e -> handleSeatSelection(seat));
        }

        // 2. Show Time Listeners
        javax.swing.JToggleButton[] timeBtns = { view.getTimeBtn1(), view.getTimeBtn2(), view.getTimeBtn3(),
                view.getTimeBtn4() };
        for (javax.swing.JToggleButton btn : timeBtns) {
            btn.addActionListener(e -> handleTimeSelection(btn));
        }

        view.getSeatTypeCombo().addActionListener(e -> updateTotalPrice());

        view.getCancelBookingButton().addActionListener(e -> view.getBookingDialog().setVisible(false));

        // 3. Date selection listeners
        view.getTodayButton().addActionListener(e -> handleDateSelection(view.getTodayButton(), "Today"));
        view.getTomorrowButton().addActionListener(e -> handleDateSelection(view.getTomorrowButton(), "Tomorrow"));

        // 4. "Generate Ticket" flow
        view.getGenerateTicketButton().addActionListener(e -> {
            if (selectedSeats.isEmpty() || selectedTimeBtn == null || selectedDate == null) {
                JOptionPane.showMessageDialog(view.getBookingDialog(),
                        "Please select seats, a time, and a date.",
                        "Incomplete Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            populateTicket();
            // Transition to Ticket Summary (card3)
            java.awt.CardLayout cl = (java.awt.CardLayout) view.getBookingDialog().getContentPane().getLayout();
            cl.show(view.getBookingDialog().getContentPane(), "card3");
        });
    }

    private void handleDateSelection(javax.swing.JToggleButton btn, String dateType) {
        boolean isSelected = btn.isSelected();

        view.getTodayButton().setSelected(false);
        view.getTodayButton().setBackground(null);
        view.getTomorrowButton().setSelected(false);
        view.getTomorrowButton().setBackground(null);

        if (isSelected) {
            btn.setSelected(true);
            btn.setBackground(new java.awt.Color(153, 255, 153)); // Light green for selected date

            LocalDate date = LocalDate.now();
            if (dateType.equals("Tomorrow")) {
                date = date.plusDays(1);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            selectedDate = date.format(formatter);
        } else {
            selectedDate = null;
        }
    }

    /**
     * Populates the ticket summary view in the booking dialog with selected
     * details.
     */
    private void populateTicket() {
        view.getMovieNameLabel().setText("Movie: " + currentMovie.getName());
        view.getTicketDateLabel().setText("Date: " + selectedDate);
        view.getTicketTimeLabel().setText("Time: " + selectedTimeBtn.getText());

        // Format selected seats into a comma-separated string
        StringBuilder seatsStr = new StringBuilder("Seat: ");
        TreeSet<String> seatNames = new TreeSet<>();
        for (javax.swing.JToggleButton seat : selectedSeats) {
            seatNames.add(seat.getText());
        }
        seatsStr.append(String.join(", ", seatNames));
        view.getTicketSeatLabel().setText(seatsStr.toString());

        view.getTicketSeatTypeLabel().setText("Seat Type: " + view.getSeatTypeCombo().getSelectedItem());
        view.getTicketPriceLabel().setText("Price: $" + view.getPriceLabel().getText());

        saveBooking(); // Persist to file
        refreshBookingTables(); // Update UI tables
    }

    /**
     * Saves the current booking details to the ticket database file.
     */
    private void saveBooking() {
        StringBuilder seatsStr = new StringBuilder();
        TreeSet<String> seatNames = new TreeSet<>();
        for (javax.swing.JToggleButton seat : selectedSeats) {
            seatNames.add(seat.getText());
        }
        seatsStr.append(String.join(", ", seatNames));

        // Construct semicolon-delimited string for storage
        String bookingData = String.join(";",
                loggedInUserIdentifier,
                currentMovie.getName(),
                currentMovie.getGenre(),
                currentMovie.getLanguage(),
                currentMovie.getRating(),
                selectedDate,
                selectedTimeBtn.getText(),
                seatsStr.toString(),
                (String) view.getSeatTypeCombo().getSelectedItem(),
                view.getPriceLabel().getText());

        if (Ticket.saveBooking(bookingData)) {
            // Success
        } else {
            JOptionPane.showMessageDialog(view, "Error saving booking!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Refreshes both the generic history table and the home dashboard recent panel.
     */
    private void refreshBookingTables() {
        List<String[]> allBookings = getAllUserBookings();
        populateMyBookingTable(new ArrayList<>(allBookings));

        javax.swing.table.DefaultTableModel homeBookingModel = (javax.swing.table.DefaultTableModel) view.getJTable1()
                .getModel();
        homeBookingModel.setRowCount(0);

        // Fill Home Recent Bookings (limit to top 3 newest)
        int count = 0;
        for (int i = allBookings.size() - 1; i >= 0 && count < 3; i--) {
            String[] b = allBookings.get(i);
            // Columns: Name, Genre, Language, Rating
            homeBookingModel.addRow(new Object[] { b[1], b[2], b[3], b[4] });
            count++;
        }
        updateUserDashboard();
    }

    private List<String[]> getAllUserBookings() {
        return Ticket.getBookingsForUser(loggedInUserIdentifier);
    }

    private void populateMyBookingTable(ArrayList<String[]> bookings) {
        javax.swing.table.DefaultTableModel userBookingModel = (javax.swing.table.DefaultTableModel) view.getJTable4()
                .getModel();
        userBookingModel.setRowCount(0);

        // Fill My Booking Table
        for (int i = bookings.size() - 1; i >= 0; i--) {
            String[] b = bookings.get(i);
            // Table columns: Name, Genre, Language, Rated, Date
            userBookingModel.addRow(new Object[] { b[1], b[2], b[3], b[4], b[5] });
        }
    }

    private void initMyBookingListeners() {
        view.getSearchButtonForMyBooking().addActionListener(e -> handleSearchMyBooking());
        view.getSortByMovieNameButtonMyBooking().addActionListener(e -> handleSortMyBookingByName());
        view.getSortByDateButtonMyBooking().addActionListener(e -> handleSortMyBookingByDate());
    }

    /**
     * Filters the user's booking history by movie name.
     */
    private void handleSearchMyBooking() {
        String query = view.getSearchBarForMyBooking().getText().toLowerCase().trim();
        List<String[]> all = getAllUserBookings();
        if (query.isEmpty()) {
            populateMyBookingTable(new ArrayList<>(all));
            return;
        }

        // Case-insensitive search on movie name (index 1)
        ArrayList<String[]> filtered = new ArrayList<>();
        for (String[] b : all) {
            if (b[1].toLowerCase().contains(query)) {
                filtered.add(b);
            }
        }
        populateMyBookingTable(filtered);
    }

    private boolean isSortNameAsc = true;

    private void handleSortMyBookingByName() {
        List<String[]> bookings = new ArrayList<>(getAllUserBookings());
        bookings.sort((a, b) -> {
            int res = a[1].compareToIgnoreCase(b[1]);
            return isSortNameAsc ? res : -res;
        });
        isSortNameAsc = !isSortNameAsc;
        populateMyBookingTable(new ArrayList<>(bookings));
    }

    private boolean isSortDateAsc = false;

    /**
     * Sorts the booking history by date.
     */
    private void handleSortMyBookingByDate() {
        List<String[]> bookings = new ArrayList<>(getAllUserBookings());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        bookings.sort((a, b) -> {
            try {
                // Parse date strings for chronological comparison
                LocalDate d1 = LocalDate.parse(a[5].trim(), formatter);
                LocalDate d2 = LocalDate.parse(b[5].trim(), formatter);
                int res = d1.compareTo(d2);
                return isSortDateAsc ? res : -res;
            } catch (Exception e) {
                return 0; // Fallback if date is unparseable
            }
        });
        isSortDateAsc = !isSortDateAsc; // Toggle direction
        populateMyBookingTable(new ArrayList<>(bookings));
    }

    private void handleSeatSelection(javax.swing.JToggleButton seat) {
        if (seat.isSelected()) {
            selectedSeats.add(seat);
            seat.setBackground(new java.awt.Color(153, 255, 153)); // Light green for selected
        } else {
            selectedSeats.remove(seat);
            seat.setBackground(null);
        }
        updateTotalPrice();
    }

    private void handleTimeSelection(javax.swing.JToggleButton btn) {
        if (selectedTimeBtn != null) {
            selectedTimeBtn.setSelected(false);
            selectedTimeBtn.setBackground(null);
        }
        if (btn.isSelected()) {
            selectedTimeBtn = btn;
            btn.setBackground(new java.awt.Color(255, 204, 153)); // Light orange for selected time
        } else {
            selectedTimeBtn = null;
        }
    }

    private void updateTotalPrice() {
        int basePrice = 0;
        String seatType = (String) view.getSeatTypeCombo().getSelectedItem();
        if ("Standard Seat".equals(seatType))
            basePrice = 185;
        else if ("Reclinear Seat".equals(seatType))
            basePrice = 225;
        else if ("Luxury Seat".equals(seatType))
            basePrice = 300;

        int total = selectedSeats.size() * basePrice;
        view.getPriceLabel().setText(String.valueOf(total));
    }

    private void configureTables() {
        view.getJTable1().setDefaultEditor(Object.class, null);
        view.getJTable4().setDefaultEditor(Object.class, null);
    }

    private void handleViewTicket() {
        int selectedRow = view.getJTable4().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Please select a booking to view!", "No Selection",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String[]> userBookings = Ticket.getBookingsForUser(loggedInUserIdentifier);
        if (userBookings.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No bookings found!", "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        int dataIndex = userBookings.size() - 1 - selectedRow;
        if (dataIndex < 0 || dataIndex >= userBookings.size()) {
            return;
        }

        String[] b = userBookings.get(dataIndex);

        // Populate labels
        view.getMovieNameLabel().setText("Movie: " + b[1]);
        view.getTicketDateLabel().setText("Date: " + b[5]);
        view.getTicketTimeLabel().setText("Time: " + b[6]);
        view.getTicketSeatLabel().setText("Seat: " + b[7]);
        view.getTicketSeatTypeLabel().setText("Seat Type: " + b[8]);
        view.getTicketPriceLabel().setText("Price: " + b[9]);

        // Show dialog and transition to ticket card (card3)
        java.awt.CardLayout cl = (java.awt.CardLayout) view.getBookingDialog().getContentPane().getLayout();
        cl.show(view.getBookingDialog().getContentPane(), "card3");
        view.getBookingDialog().pack();
        view.getBookingDialog().setLocationRelativeTo(view);
        view.getBookingDialog().setVisible(true);
    }

    /**
     * Updates the user's dashboard labels with aggregate statistics.
     */
    private void updateUserDashboard() {
        // Total library movies
        view.getUserTotalMoviesLabel().setText(String.valueOf(movieList.size()));

        // Aggregate booking count and expenditure
        int bookingCount = 0;
        double totalSpent = 0.0;

        List<String[]> userBookings = Ticket.getBookingsForUser(loggedInUserIdentifier);
        for (String[] parts : userBookings) {
            bookingCount++;
            try {
                // Sum price values (index 9)
                double price = Double.parseDouble(parts[9].trim());
                totalSpent += price;
            } catch (NumberFormatException e) {
                // Skip entries with invalid price formatting
            }
        }

        view.getUserBookingCountLabel().setText(String.valueOf(bookingCount));
        view.getMoneySpentOnTicketLabel().setText(String.valueOf((int) totalSpent));
    }
}
