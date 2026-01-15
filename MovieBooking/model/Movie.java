package MovieBooking.model;

/**
 * Movie model class to represent movie data.
 * This class stores information about movies including name, director, genre,
 * language, duration, rating, and poster image path.
 */
public class Movie {
    private String name;
    private String director;
    private String genre;
    private String language;
    private String duration;
    private String rating;
    private String imagePath;

    /**
     * Constructs a Movie object with all details.
     * 
     * @param name      Name of the movie
     * @param director  Director of the movie
     * @param genre     Genre of the movie
     * @param language  Language of the movie
     * @param duration  Duration (e.g., "120 min")
     * @param rating    Audience rating (e.g., "PG-13")
     * @param imagePath Path to the poster image file
     */
    public Movie(String name, String director, String genre, String language, String duration, String rating,
            String imagePath) {
        this.name = name;
        this.director = director;
        this.genre = genre;
        this.language = language;
        this.duration = duration;
        this.rating = rating;
        this.imagePath = imagePath;
    }

    /**
     * Constructs a Movie object without an image path.
     */
    public Movie(String name, String director, String genre, String language, String duration, String rating) {
        this(name, director, genre, language, duration, rating, "");
    }

    // --- Getters ---

    /** @return Movie name */
    public String getName() {
        return name;
    }

    /** @return Director name */
    public String getDirector() {
        return director;
    }

    /** @return Movie genre */
    public String getGenre() {
        return genre;
    }

    /** @return Movie language */
    public String getLanguage() {
        return language;
    }

    /** @return Duration string */
    public String getDuration() {
        return duration;
    }

    /** @return Audience rating */
    public String getRating() {
        return rating;
    }

    /** @return Poster image path */
    public String getImagePath() {
        return imagePath;
    }

    // --- Setters ---

    /** @param name Sets movie name */
    public void setName(String name) {
        this.name = name;
    }

    /** @param director Sets director name */
    public void setDirector(String director) {
        this.director = director;
    }

    /** @param genre Sets movie genre */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /** @param language Sets movie language */
    public void setLanguage(String language) {
        this.language = language;
    }

    /** @param duration Sets movie duration */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /** @param rating Sets audience rating */
    public void setRating(String rating) {
        this.rating = rating;
    }

    /** @param imagePath Sets poster image path */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
