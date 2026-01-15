package MovieBooking.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * User class handles user registration, authentication, and profile management.
 * It uses simple file storage (users.txt) to persist user data.
 */
@SuppressWarnings("unused")
public class User {
    private String username;
    private String email;
    private String password;
    private LocalDate registrationDate;
    private String status; // "Active" or "Blocked"

    /** Path to the user database file */
    private static final String USER_FILE = "src/MovieBooking/users.txt";

    /**
     * Constructs a new User (usually for registration).
     * 
     * @param username Unique display name
     * @param email    Email identifier
     * @param password Account password
     */
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.registrationDate = LocalDate.now();
        this.status = "Active";
    }

    /**
     * Constructs a User with a specific registration date.
     */
    public User(String username, String email, String password, LocalDate registrationDate) {
        this(username, email, password, registrationDate, "Active");
    }

    /**
     * Full constructor for User objects.
     */
    public User(String username, String email, String password, LocalDate registrationDate, String status) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    /**
     * Saves a new user to the text file.
     * 
     * @param username User's chosen username
     * @param email    User's email
     * @param password User's password
     * @return true if save was successful
     */
    public static boolean saveUser(String username, String email, String password) {
        try (FileWriter writer = new FileWriter(USER_FILE, true)) {
            LocalDate registrationDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // CSV Format: username,email,password,date,status
            writer.write(
                    username + "," + email + "," + password + "," + registrationDate.format(formatter) + ",Active\n");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Authenticates a user by checking credentials against the file.
     * 
     * @param identifier Username or Email
     * @param password   Password to verify
     * @return true if credentials are valid
     */
    public static boolean authenticateUser(String identifier, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String username = parts[0];
                    String email = parts[1];
                    String userPassword = parts[2];

                    if ((identifier.equalsIgnoreCase(username) || identifier.equalsIgnoreCase(email))
                            && password.equals(userPassword)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    /**
     * Checks if a user is currently blocked by an administrator.
     * 
     * @param identifier Username or Email
     * @return true if the user's status is "Blocked"
     */
    public static boolean isUserBlocked(String identifier) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String username = parts[0];
                    String email = parts[1];
                    String status = parts[4];

                    if ((identifier.equalsIgnoreCase(username) || identifier.equalsIgnoreCase(email))
                            && "Blocked".equalsIgnoreCase(status)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    /**
     * Updates the password for a user identified by email.
     * 
     * @param email       User's email
     * @param newPassword The new password to set
     * @return true if update was successful
     */
    public static boolean updatePassword(String email, String newPassword) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].equals(email)) {
                    // Update password, preserve other fields
                    String username = parts[0];
                    String date = parts.length > 3 ? parts[3] : LocalDate.now().toString();
                    String status = parts.length > 4 ? parts[4] : "Active";
                    lines.add(username + "," + email + "," + newPassword + "," + date + "," + status);
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            return false;
        }

        if (found) {
            return writeLines(lines);
        }
        return false;
    }

    /**
     * Checks if a username or email is already taken.
     */
    public static boolean userExists(String username, String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    if (parts[0].equals(username) || parts[1].equals(email)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    /**
     * Retreives a full User object from the file.
     * 
     * @param identifier Username or Email
     * @return User object found, or null
     */
    public static User getUserDetails(String identifier) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String username = parts[0];
                    String email = parts[1];
                    String password = parts[2];

                    if (identifier.equals(username) || identifier.equals(email)) {
                        LocalDate date = LocalDate.now();
                        if (parts.length > 3) {
                            try {
                                date = LocalDate.parse(parts[3], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            } catch (Exception e) {
                                // Fallback to current date if parse fails
                            }
                        }
                        String status = parts.length > 4 ? parts[4] : "Active";
                        return new User(username, email, password, date, status);
                    }
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * Updates user profile information.
     */
    public static boolean updateUser(String oldEmail, String newUsername, String newEmail, String newPassword) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].equals(oldEmail)) {
                    String date = parts.length > 3 ? parts[3] : LocalDate.now().toString();
                    String status = parts.length > 4 ? parts[4] : "Active";
                    lines.add(newUsername + "," + newEmail + "," + newPassword + "," + date + "," + status);
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            return false;
        }

        if (found) {
            return writeLines(lines);
        }
        return false;
    }

    /**
     * Updates the block/active status of a user.
     */
    public static boolean updateStatus(String email, String newStatus) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].equals(email)) {
                    // Preserve all fields, update status (index 4) or append it
                    String username = parts[0];
                    String password = parts[2];
                    String date = parts.length > 3 ? parts[3] : LocalDate.now().toString();
                    lines.add(username + "," + email + "," + password + "," + date + "," + newStatus);
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            return false;
        }

        if (found) {
            return writeLines(lines);
        }
        return false;
    }

    /**
     * Deletes a user record from the file.
     */
    public static boolean deleteUser(String emailToDelete) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    if (parts[1].equals(emailToDelete)) {
                        found = true;
                        continue; // Skip this line to "delete" it
                    }
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            return false;
        }

        if (found) {
            return writeLines(lines);
        }
        return false;
    }

    /**
     * Internal helper to write multiple lines back to the user file.
     */
    private static boolean writeLines(List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all users currently registered in the system.
     * 
     * @return List of all User objects
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String username = parts[0];
                    String email = parts[1];
                    String password = parts[2];
                    LocalDate date = LocalDate.now();
                    if (parts.length > 3) {
                        try {
                            date = LocalDate.parse(parts[3], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        } catch (Exception e) {
                            // Fallback for malformed dates
                        }
                    }
                    String status = parts.length > 4 ? parts[4] : "Active";
                    users.add(new User(username, email, password, date, status));
                }
            }
        } catch (IOException e) {
            return users;
        }
        return users;
    }

    // --- Getters ---

    /** @return Username string */
    public String getUsername() {
        return username;
    }

    /** @return User email */
    public String getEmail() {
        return email;
    }

    /** @return Plain-text password (use with caution) */
    public String getPassword() {
        return password;
    }

    /** @return Date the user registered */
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    /** @return "Active" or "Blocked" */
    public String getStatus() {
        return status;
    }
}
