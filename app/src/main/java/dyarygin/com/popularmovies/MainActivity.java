package dyarygin.com.popularmovies;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        FragmentManager fm = getSupportFragmentManager();
//        Fragment fragment = fm.findFragmentById(R.layout.fragment_main);
//        if (fragment == null) {
//            fragment = new MainActivityFragment();
//            fm.beginTransaction()
//                    .add(R.id.frame_container, fragment)
//                    .commit();
//        }




        Stetho.initializeWithDefaults(this);
    }
}
