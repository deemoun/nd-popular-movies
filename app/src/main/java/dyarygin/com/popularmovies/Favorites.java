package dyarygin.com.popularmovies;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Favorites extends RealmObject{
    @PrimaryKey
    private String movieId;
    private boolean isFavorite;

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
}
