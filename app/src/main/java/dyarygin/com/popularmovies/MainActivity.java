package dyarygin.com.popularmovies;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_holder);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        FragmentManager fragmentManager = getFragmentManager();
        Fragment mainFragment = new MainActivityFragment();

        fragmentManager.beginTransaction()
                .add(R.id.fragmentHolder, mainFragment)
                .commit();

        Stetho.initializeWithDefaults(this);
    }
}
