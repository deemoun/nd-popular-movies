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

        if(savedInstanceState == null) {
            setContentView(R.layout.activity_detail);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            DetailActivityFragment fragment = new DetailActivityFragment();
//            fragment.setArguments(arguments);

//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragmentDetailContainer, fragment)
//                    .commit();
        }

    }

    public String getMovieId(){
        return getIntent().getStringExtra(Config.EXTRA_MOVIEID);
    }

}
