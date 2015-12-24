package dyarygin.com.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (findViewById(R.id.fragment_container) != null) {
//            if (savedInstanceState != null) {
//                return;
//            }
//            FragmentManager fragmentManager = getFragmentManager();
//
//            fragmentManager.beginTransaction()
//                    .add(R.id.fragment_container, new DetailActivityFragment())
//                    .commit();
//        }

        Stetho.initializeWithDefaults(this);
    }
}
