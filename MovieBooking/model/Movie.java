package MovieBooking.model;

/**
 * Movie model class to represent movie data
 */
public class Movie {
    private String name;
    private String director;
    private String genre;
    private String language;
    private String duration;
    private String rating;

    /**
     *
     * @param name
     * @param director
     * @param genre
     * @param language
     * @param duration
     * @param rating
     */
    public Movie(String name, String director, String genre, String language, String duration, String rating) {
        this.name = name;
        this.director = director;
        this.genre = genre;
        this.language = language;
        this.duration = duration;
        this.rating = rating;
    }

    // Getters

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public String getDirector() {
        return director;
    }

    /**
     *
     * @return
     */
    public String getGenre() {
        return genre;
    }

    /**
     *
     * @return
     */
    public String getLanguage() {
        return language;
    }

    /**
     *
     * @return
     */
    public String getDuration() {
        return duration;
    }

    /**
     *
     * @return
     */
    public String getRating() {
        return rating;
    }

    // Setters

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param director
     */
    public void setDirector(String director) {
        this.director = director;
    }

    /**
     *
     * @param genre
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     *
     * @param language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     *
     * @param duration
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     *
     * @param rating
     */
    public void setRating(String rating) {
        this.rating = rating;
    }
}