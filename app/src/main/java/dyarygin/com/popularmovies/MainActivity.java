package dyarygin.com.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.movieSelectorListener {

    public boolean ismTwoPane() {
        if (findViewById(R.id.fragmentMovieHolder) != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragmentMovieHolder) != null) {
            if (ismTwoPane()) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentMovieHolder, new EmptyDetailViewFragment(), "TAG")
                        .commit();
            }
        }
        Stetho.initializeWithDefaults(this);
    }

    public void onMovieSelected(String movieId) {
        if (ismTwoPane()) {
            Bundle mBundle = new Bundle();
            mBundle.putString(Utils.EXTRA_MOVIEID, movieId);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(mBundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentMovieHolder, fragment, "TAG")
                    .commit();

        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(Utils.EXTRA_MOVIEID, movieId);
            startActivity(intent);
        }
    }
}
