package dyarygin.com.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MoviesDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLHelper dbHelper;
    private String[] allColumns = { MySQLHelper.COLUMN_ID,
            MySQLHelper.COLUMN_MOVIE_TITLE };

    public MoviesDataSource(Context context) {
        dbHelper = new MySQLHelper(context);
    }

    public void open () {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Movie createMovie(String movie) {
        ContentValues values = new ContentValues();
        values.put(MySQLHelper.COLUMN_MOVIE_TITLE, movie);
        long insertId = database.insert(MySQLHelper.TABLE_MOVIES, null,
                values);
        Cursor cursor = database.query(MySQLHelper.TABLE_MOVIES,
                allColumns, MySQLHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Movie newMovie = cursorToMovie(cursor);
        cursor.close();
        return newMovie;
    }

    public void deleteMovie(Movie movie) {
        long id = movie.getId();
        System.out.println("Movie deleted with id: " + id);
        database.delete(MySQLHelper.TABLE_MOVIES, MySQLHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<Movie>();

        Cursor cursor = database.query(MySQLHelper.TABLE_MOVIES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Movie movie = cursorToMovie(cursor);
            movies.add(movie);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return movies;
    }

    private Movie cursorToMovie(Cursor cursor) {
        Movie movie = new Movie();
        movie.setId(cursor.getLong(0));
        movie.setMovieOriginalTitle(cursor.getString(1));
        return movie;
    }
}
