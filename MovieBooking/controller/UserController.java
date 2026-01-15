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
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import MovieBooking.model.User;
import MovieBooking.model.Movie;
import MovieBooking.view.MovieBookingView;
import MovieBooking.view.AuthenticationView;

public class UserController {
    private MovieBookingView view;
    private Set<javax.swing.JButton> activeButtons;
    private String loggedInUserIdentifier;
    private ArrayList<Movie> movieList;
    private JPanel dynamicGalleryPanel; // New panel for movies only
    private static final String MOVIE_FILE = "src/MovieBooking/movies.txt";

    public UserController(MovieBookingView view, String loggedInUserIdentifier) {
        this.view = view;
        this.loggedInUserIdentifier = loggedInUserIdentifier;
        this.activeButtons = new HashSet<>();
        this.movieList = new ArrayList<>();
        java.awt.CardLayout cl = (java.awt.CardLayout) view.getContentPane().getLayout();
        cl.show(view.getContentPane(), "card3");

        initBrowsePageLayout();
        initUserController();
        updateWelcomeBar();
        loadMoviesFromFile(); // Ensure movies are loaded
        populateFilters(true); // Force "all" selection on first load
        showUserHome();
    }

    private void initUserController() {
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
    }

    private void setupPageButtons(javax.swing.JButton homeBtn, javax.swing.JButton moviesBtn,
            javax.swing.JButton bookingBtn, javax.swing.JButton profileBtn, javax.swing.JButton logoutBtn) {
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
        showCard("card5", view.getUsersButton2(), view.getUsersButton4(), view.getUsersButton6(),
                view.getUsersButton8());
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
        activeButtons.clear();
        for (javax.swing.JButton btn : buttons) {
            btn.setOpaque(true);
            btn.setBackground(new java.awt.Color(229, 9, 20));
            btn.setForeground(new java.awt.Color(255, 255, 255));
            activeButtons.add(btn);
        }
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
                view.getProfileMemberSinceLabel()
                        .setText("Member Since: " + user.getRegistrationDate().format(dateFormatter));
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

    private void initBrowsePageLayout() {
        JPanel mainPanel = view.getMoviePanelContainer(); // This is jPanel7

        // Save references to existing components from jPanel7
        java.awt.Component[] components = mainPanel.getComponents();

        // Find specific components we want to keep in the header
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

        // If we didn't find the label by text, try to find it by name
        if (recentLabel == null) {
            for (java.awt.Component comp : components) {
                if (comp.getName() != null && comp.getName().equals("RecentBooking4")) {
                    recentLabel = (JLabel) comp;
                    break;
                }
            }
        }

        // Clear the main panel to reset layout
        mainPanel.removeAll();
        mainPanel.setLayout(new java.awt.BorderLayout(0, 0));

        // Create Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new java.awt.Color(249, 249, 249));
        headerPanel.setLayout(new javax.swing.BoxLayout(headerPanel, javax.swing.BoxLayout.Y_AXIS));
        headerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 26, 10, 26));

        // Row 1: Title
        if (browseTitle == null) {
            browseTitle = new JLabel("Browse Movies");
            browseTitle.setFont(new java.awt.Font("Segoe UI", 1, 24));
        }
        browseTitle.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        headerPanel.add(browseTitle);
        headerPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 15)));

        // Row 2: Search Bar
        JPanel searchRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        searchRow.setOpaque(false);
        sBar.setPreferredSize(new java.awt.Dimension(300, 32));
        searchRow.add(sBar);
        searchRow.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(18, 0)));
        searchRow.add(sBtn);
        searchRow.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        headerPanel.add(searchRow);
        headerPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 15)));

        // Row 3: "Now Showing" Label (Above filters as requested)
        if (recentLabel == null) {
            recentLabel = new JLabel("Now Showing");
            recentLabel.setFont(new java.awt.Font("Segoe UI", 1, 18));
        } else {
            recentLabel.setText("Now Showing");
        }
        recentLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        headerPanel.add(recentLabel);
        headerPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));

        // Row 4: Filters
        JPanel filterRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        filterRow.setOpaque(false);
        if (filterLabel1 == null)
            filterLabel1 = new JLabel("genre: ");
        else
            filterLabel1.setText("genre: "); // Lowercase label

        if (filterLabel2 == null)
            filterLabel2 = new JLabel("language: ");
        else
            filterLabel2.setText("language: "); // Lowercase label

        filterRow.add(filterLabel1);
        filterRow.add(fGenre);
        filterRow.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(18, 0)));
        filterRow.add(filterLabel2);
        filterRow.add(fLang);
        filterRow.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        headerPanel.add(filterRow);
        headerPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 20)));

        // Gallery Panel
        dynamicGalleryPanel = new JPanel();
        dynamicGalleryPanel.setBackground(new java.awt.Color(249, 249, 249));
        // Exactly 2 columns
        dynamicGalleryPanel.setLayout(new java.awt.GridLayout(0, 2, 30, 30));

        mainPanel.add(headerPanel, java.awt.BorderLayout.NORTH);
        mainPanel.add(dynamicGalleryPanel, java.awt.BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private boolean isPopulatingFilters = false;

    private void populateFilters(boolean forceAll) {
        isPopulatingFilters = true;

        String currentGenre = null;
        String currentLang = null;

        if (!forceAll) {
            currentGenre = (String) view.getFilterGenre().getSelectedItem();
            currentLang = (String) view.getFilterLanguage().getSelectedItem();
        }

        view.getFilterGenre().removeAllItems();
        view.getFilterLanguage().removeAllItems();

        view.getFilterGenre().addItem("all genre");
        view.getFilterLanguage().addItem("all language");

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

    private void initSearchAndFilters() {
        // Search triggers only on button click
        view.getSearchButton().addActionListener(e -> handleFiltering());

        // Dropdown filters remain instant
        view.getFilterGenre().addActionListener(e -> {
            if (!isPopulatingFilters)
                handleFiltering();
        });

        view.getFilterLanguage().addActionListener(e -> {
            if (!isPopulatingFilters)
                handleFiltering();
        });
    }

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

    private javax.swing.JPanel createMoviePanel(Movie movie) {
        // Theme Colors
        java.awt.Color themeBackground = new java.awt.Color(249, 249, 249);
        java.awt.Color themeRed = new java.awt.Color(229, 9, 20);

        // Outer Panel
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setBackground(themeBackground);
        // Using theme red for border
        panel.setBorder(javax.swing.BorderFactory.createLineBorder(themeRed));
        panel.setLayout(new java.awt.BorderLayout());
        panel.setPreferredSize(new java.awt.Dimension(250, 420));

        // Poster Section
        javax.swing.JPanel posterPanel = new javax.swing.JPanel();
        posterPanel.setBackground(java.awt.Color.WHITE); // Keep poster background white for contrast
        posterPanel.setPreferredSize(new java.awt.Dimension(250, 201));
        posterPanel.setLayout(new java.awt.BorderLayout());
        // Red separator line
        posterPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, themeRed));

        if (movie.getImagePath() != null && !movie.getImagePath().isEmpty()) {
            try {
                javax.swing.ImageIcon icon = new javax.swing.ImageIcon(movie.getImagePath());
                // Scale image to fit poster area
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

        // Detail Section
        javax.swing.JPanel detailPanel = new javax.swing.JPanel();
        // Light red tint for the background to match theme
        detailPanel.setBackground(new java.awt.Color(255, 245, 245));
        detailPanel.setLayout(new javax.swing.BoxLayout(detailPanel, javax.swing.BoxLayout.Y_AXIS));
        detailPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 19, 10, 19));

        javax.swing.JLabel nameLabel = new javax.swing.JLabel(movie.getName());
        nameLabel.setForeground(themeRed); // Red title
        nameLabel.setFont(new java.awt.Font("Segoe UI", 1, 14));

        javax.swing.JLabel genreLabel = new javax.swing.JLabel("Genre: " + movie.getGenre());
        genreLabel.setForeground(java.awt.Color.DARK_GRAY);

        javax.swing.JLabel timeLabel = new javax.swing.JLabel("Time: " + movie.getDuration());
        timeLabel.setForeground(java.awt.Color.DARK_GRAY);

        javax.swing.JLabel langLabel = new javax.swing.JLabel("Language: " + movie.getLanguage());
        langLabel.setForeground(java.awt.Color.DARK_GRAY);

        javax.swing.JButton bookBtn = new javax.swing.JButton("Book Now");
        bookBtn.setBackground(themeRed);
        bookBtn.setForeground(java.awt.Color.WHITE);
        bookBtn.setFont(new java.awt.Font("Segoe UI", 1, 12));
        bookBtn.setPreferredSize(new java.awt.Dimension(100, 30));
        bookBtn.setFocusPainted(false);

        // Container to center the button horizontally
        javax.swing.JPanel buttonContainer = new javax.swing.JPanel(
                new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        buttonContainer.setOpaque(false); // Transparent to show detailPanel color
        buttonContainer.add(bookBtn);

        // Add spacing between labels
        detailPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 5)));
        detailPanel.add(nameLabel);
        detailPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 8)));
        detailPanel.add(genreLabel);
        detailPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 3)));
        detailPanel.add(timeLabel);
        detailPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 3)));
        detailPanel.add(langLabel);
        // Larger space before the button
        detailPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 20)));
        detailPanel.add(buttonContainer);

        panel.add(posterPanel, java.awt.BorderLayout.NORTH);
        panel.add(detailPanel, java.awt.BorderLayout.CENTER);

        return panel;
    }
}
