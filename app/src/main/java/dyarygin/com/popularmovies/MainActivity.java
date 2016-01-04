package dyarygin.com.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.movieSelectorListener {

    public boolean ismTwoPane() {
        if (findViewById(R.id.fragmentDetailContainer) != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragmentDetailContainer) != null) {
            if (ismTwoPane()){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentDetailContainer, new TabletDetailActivityFragment(), "TAG")
                        .commit();
            }
        }
        Stetho.initializeWithDefaults(this);
    }

    public void onMovieSelected(String movieId){
        Toast.makeText(getApplicationContext(), movieId, Toast.LENGTH_SHORT).show();
    }

}
