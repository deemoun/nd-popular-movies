package dyarygin.com.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragmentDetailContainer) != null) {
            mTwoPane = true;
            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentDetailContainer, new TabletDetailActivityFragment(), "TAG")
                        .commit();
            } else {
                mTwoPane = false;
            }
        }
        Stetho.initializeWithDefaults(this);
    }

    @Override
    public void onItemSelected(String movieId) {

    }
}
