package dyarygin.com.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class DetailActivity extends AppCompatActivity {

    public Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public String getMovieImage() {
        String movieImage = getIntent().getStringExtra(MainActivityFragment.EXTRA_MOVIEIMAGE);
        String noMovieImage = "R.drawable.no_movie_image";
        if (movieImage.endsWith("null")){
            return noMovieImage;
        } else return movieImage;
    }

    public String getMovieBackdropPath() {
        return getIntent().getStringExtra(MainActivityFragment.EXTRA_MOVIEBACKDROPPATH);
    }

    public String getVoteAverage() {
        return getIntent().getStringExtra(MainActivityFragment.EXTRA_MOVIEVOTE);
    }

    public String getMovieId(){
        return getIntent().getStringExtra(MainActivityFragment.EXTRA_MOVIEID);
    }

    public String getReleaseDate() {
        return getIntent().getStringExtra(MainActivityFragment.EXTRA_MOVIERELEASEDATE);
    }

    public String getOriginalTitle() {
        return getIntent().getStringExtra(MainActivityFragment.EXTRA_MOVIEORIGINALTITLE);
    }

    public String getMovieOverview() {
        return getIntent().getStringExtra(MainActivityFragment.EXTRA_MOVIEOVERVIEW);
    }

}
