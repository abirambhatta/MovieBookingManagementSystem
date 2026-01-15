package MovieBooking.model;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ticket model handles ticket booking data, file storage, and reporting.
 * It encapsulates all information related to a movie booking transaction.
 */
public class Ticket {
    private String username;
    private String movieName;
    private String genre;
    private String language;
    private String rating;
    private String date;
    private String time;
    private String seats;
    private String seatType;
    private String price;

    /** Path to the booking records file */
    private static final String BOOKING_FILE = "src/MovieBooking/ticket.txt";

    /**
     * Constructs a Ticket object from a raw data array.
     * 
     * @param parts String array containing booking details (split from file line)
     */
    public Ticket(String[] parts) {
        if (parts.length >= 10) {
            this.username = parts[0];
            this.movieName = parts[1];
            this.genre = parts[2];
            this.language = parts[3];
            this.rating = parts[4];
            this.date = parts[5];
            this.time = parts[6];
            this.seats = parts[7];
            this.seatType = parts[8];
            this.price = parts[9];
        }
    }

    /**
     * Calculates the number of bookings per user by scanning the database.
     * 
     * @return A map of usernames to their respective booking counts.
     */
    public static Map<String, Integer> getBookingCounts() {
        Map<String, Integer> counts = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKING_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(";");
                if (parts.length > 0) {
                    String username = parts[0].trim();
                    counts.put(username, counts.getOrDefault(username, 0) + 1);
                }
            }
        } catch (IOException e) {
            // File might not exist yet if no bookings have been made
        }
        return counts;
    }

    /**
     * Reads all bookings from the persistent storage file.
     * 
     * @return List of string arrays, each representing a full booking record.
     */
    public static List<String[]> getAllBookings() {
        List<String[]> bookings = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKING_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(";");
                if (parts.length >= 10) {
                    bookings.add(parts);
                }
            }
        } catch (IOException e) {
            // Returns empty list if file cannot be read
        }
        return bookings;
    }

    /**
     * Filters for bookings related to a specific user.
     * 
     * @param userIdentifier Username or Email to search for
     * @return List of booking records for that user.
     */
    public static List<String[]> getBookingsForUser(String userIdentifier) {
        List<String[]> userBookings = new ArrayList<>();
        for (String[] b : getAllBookings()) {
            if (b[0].equalsIgnoreCase(userIdentifier)) {
                userBookings.add(b);
            }
        }
        return userBookings;
    }

    /**
     * Identifies the most recent movie booked by a user.
     * 
     * @param userIdentifier Username or Email
     * @return Name of the most recently booked movie, or "N/A"
     */
    public static String getRecentMovieByUser(String userIdentifier) {
        List<String[]> bookings = getBookingsForUser(userIdentifier);
        if (bookings.isEmpty())
            return "N/A";
        // Assuming the last entry in the file is chronological
        return bookings.get(bookings.size() - 1)[1];
    }

    /**
     * Summarizes the total financial expenditure for a user.
     * 
     * @param userIdentifier Username or Email
     * @return Total amount spent string (formatted as integer string).
     */
    public static String getTotalSpentByUser(String userIdentifier) {
        List<String[]> bookings = getBookingsForUser(userIdentifier);
        int total = 0;
        for (String[] b : bookings) {
            try {
                total += Integer.parseInt(b[9].trim());
            } catch (Exception e) {
                // Skip malformed price data
            }
        }
        return String.valueOf(total);
    }

    /**
     * Appends a new booking record to the persistence file.
     * 
     * @param bookingData Raw semi-colon concatenated booking status.
     * @return true if write operation succeeded.
     */
    public static boolean saveBooking(String bookingData) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKING_FILE, true))) {
            writer.println(bookingData);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // --- Getters ---

    /** @return Username of the ticket owner */
    public String getUsername() {
        return username;
    }

    /** @return Name of the movie */
    public String getMovieName() {
        return movieName;
    }

    /** @return Movie genre */
    public String getGenre() {
        return genre;
    }

    /** @return Movie language */
    public String getLanguage() {
        return language;
    }

    /** @return Audience rating */
    public String getRating() {
        return rating;
    }

    /** @return Booking date */
    public String getDate() {
        return date;
    }

    /** @return Show time */
    public String getTime() {
        return time;
    }

    /** @return Selected seats (comma separated) */
    public String getSeats() {
        return seats;
    }

    /** @return Type of seat (e.g., Gold, Platinum) */
    public String getSeatType() {
        return seatType;
    }

    /** @return Total price of the ticket */
    public String getPrice() {
        return price;
    }
}
