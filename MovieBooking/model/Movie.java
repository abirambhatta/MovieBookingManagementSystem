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
    private String imagePath;

    public Movie(String name, String director, String genre, String language, String duration, String rating, String imagePath) {
        this.name = name;
        this.director = director;
        this.genre = genre;
        this.language = language;
        this.duration = duration;
        this.rating = rating;
        this.imagePath = imagePath;
    }

    public Movie(String name, String director, String genre, String language, String duration, String rating) {
        this(name, director, genre, language, duration, rating, "");
    }

    public String getName() {
        return name;
    }

    public String getDirector() {
        return director;
    }

    public String getGenre() {
        return genre;
    }

    public String getLanguage() {
        return language;
    }

    public String getDuration() {
        return duration;
    }

    public String getRating() {
        return rating;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
