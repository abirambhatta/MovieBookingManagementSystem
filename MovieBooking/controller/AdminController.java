package MovieBooking.controller;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.io.*;

import MovieBooking.model.Movie;
import MovieBooking.view.MovieBookingView;
import MovieBooking.view.AuthenticationView;

/**
 *
 * @author lenovo
 */
public class AdminController {
    private MovieBookingView view;
    private CardLayout cardLayout;
    private ArrayList<Movie> movieList;
    private static final String MOVIE_FILE = "src/MovieBooking/movies.txt";
    private String currentPage = "home";
    private static final String HOME_CARD = "card3";
    private static final String MOVIES_CARD = "card2";
    private static final String USERS_CARD = "card4";
    private javax.swing.JButton activeButton;

    /**
     *
     * @param view
     */
    public AdminController(MovieBookingView view) {
        this.view = view;
        this.cardLayout = (CardLayout) view.getContentPanel().getLayout();
        this.movieList = new ArrayList<>();
        initController();
        loadMoviesFromFile();
        loadMovieTable();
        currentPage = "movies";
        cardLayout.show(view.getContentPanel(), MOVIES_CARD);
        activeButton = view.getMoviesButton();
        setActiveButton(view.getMoviesButton());
        configureTable();
    }

    private void configureTable() {
        view.getMovieTable().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        view.getMovieTable().setRowSelectionAllowed(true);
        view.getMovieTable().setColumnSelectionAllowed(false);
    }

    private void initController() {
        // Navigation button event handlers
        view.getHomeButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHomeCard();
            }
        });
        addButtonHoverListeners(view.getHomeButton());

        view.getMoviesButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMoviesCard();
            }
        });
        addButtonHoverListeners(view.getMoviesButton());

        view.getUsersButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showUsersCard();
            }
        });
        addButtonHoverListeners(view.getUsersButton());

        view.getLogoutButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performLogout();
            }
        });

        // Movie CRUD operation button handlers
        view.getAddButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addMovie();
            }
        });

        view.getUpdateButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateMovie();
            }
        });

        view.getDeleteButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteMovie();
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
                }
            }
        });
    }

    private void showHomeCard() {
        cardLayout.show(view.getContentPanel(), HOME_CARD);
        currentPage = "home";
        setActiveButton(view.getHomeButton());
    }

    private void showMoviesCard() {
        cardLayout.show(view.getContentPanel(), MOVIES_CARD);
        currentPage = "movies";
        setActiveButton(view.getMoviesButton());
    }

    private void showUsersCard() {
        cardLayout.show(view.getContentPanel(), USERS_CARD);
        currentPage = "users";
        setActiveButton(view.getUsersButton());
    }

    private void setActiveButton(javax.swing.JButton button) {
        if (activeButton != null) {
            activeButton.setOpaque(false);
            activeButton.setForeground(new java.awt.Color(0, 0, 0));
        }
        activeButton = button;
        button.setOpaque(true);
        button.setBackground(new java.awt.Color(229, 9, 20));
        button.setForeground(new java.awt.Color(255, 255, 255));
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
                int firstComma = line.indexOf(',');
                int secondComma = line.indexOf(',', firstComma + 1);
                int thirdComma = line.indexOf(',', secondComma + 1);
                int fourthComma = line.indexOf(',', thirdComma + 1);
                int fifthComma = line.indexOf(',', fourthComma + 1);

                if (firstComma != -1 && secondComma != -1 && thirdComma != -1 &&
                        fourthComma != -1 && fifthComma != -1) {
                    String name = line.substring(0, firstComma);
                    String director = line.substring(firstComma + 1, secondComma);
                    String genre = line.substring(secondComma + 1, thirdComma);
                    String language = line.substring(thirdComma + 1, fourthComma);
                    String duration = line.substring(fourthComma + 1, fifthComma);
                    String rating = line.substring(fifthComma + 1);

                    movieList.add(new Movie(name, director, genre, language, duration, rating));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view, "Error loading movies from file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveMoviesToFile() {
        try (FileWriter writer = new FileWriter(MOVIE_FILE)) {
            for (Movie movie : movieList) {
                writer.write(movie.getName() + "," + movie.getDirector() + "," +
                        movie.getGenre() + "," + movie.getLanguage() + "," +
                        movie.getDuration() + "," + movie.getRating() + "\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view, "Error saving movies to file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMovieTable() {
        DefaultTableModel model = (DefaultTableModel) view.getMovieTable().getModel();
        model.setRowCount(0);

        for (Movie movie : movieList) {
            Object[] row = {
                    movie.getName(),
                    movie.getDirector(),
                    movie.getGenre(),
                    movie.getLanguage(),
                    movie.getDuration(),
                    movie.getRating()
            };
            model.addRow(row);
        }
    }

    private void addMovie() {
        DefaultTableModel model = (DefaultTableModel) view.getMovieTable().getModel();
        model.addRow(new Object[] { "", "", "", "", "", "" });
        int newRow = model.getRowCount() - 1;
        view.getMovieTable().setRowSelectionInterval(newRow, newRow);
        view.getMovieTable().editCellAt(newRow, 0);
        view.getMovieTable().getEditorComponent().requestFocus();
    }

    private void deleteMovie() {
        int selectedRow = view.getMovieTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Please select a movie to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete this movie?",
                "Delete Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DefaultTableModel model = (DefaultTableModel) view.getMovieTable().getModel();
            model.removeRow(selectedRow);
            JOptionPane.showMessageDialog(view, "Movie deleted successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateMovie() {
        DefaultTableModel model = (DefaultTableModel) view.getMovieTable().getModel();
        movieList.clear();

        for (int i = 0; i < model.getRowCount(); i++) {
            String name = String.valueOf(model.getValueAt(i, 0)).trim();
            String director = String.valueOf(model.getValueAt(i, 1)).trim();
            String genre = String.valueOf(model.getValueAt(i, 2)).trim();
            String language = String.valueOf(model.getValueAt(i, 3)).trim();
            String duration = String.valueOf(model.getValueAt(i, 4)).trim();
            String rating = String.valueOf(model.getValueAt(i, 5)).trim();

            if (name.isEmpty() || director.isEmpty() || genre.isEmpty() ||
                    language.isEmpty() || duration.isEmpty() || rating.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Row " + (i + 1) + " has empty fields!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            movieList.add(new Movie(name, director, genre, language, duration, rating));
        }

        saveMoviesToFile();
        JOptionPane.showMessageDialog(view, "Movies updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}