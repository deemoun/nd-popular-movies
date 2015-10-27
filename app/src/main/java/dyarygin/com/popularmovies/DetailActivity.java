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
        intent = getIntent();
        return intent.getStringExtra(MainActivityFragment.EXTRA_MOVIEIMAGE);
    }

    public String getVoteAverage() {
        intent = getIntent();
        return intent.getStringExtra(MainActivityFragment.EXTRA_MOVIEVOTE);
    }

}
