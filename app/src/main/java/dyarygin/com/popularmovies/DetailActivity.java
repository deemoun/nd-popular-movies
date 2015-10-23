package dyarygin.com.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class DetailActivity extends AppCompatActivity {

    private Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String movieImage = intent.getStringExtra(MainActivityFragment.EXTRA_MOVIEURL);

        DetailActivityFragment fragobj = new DetailActivityFragment();
        bundle.putString("message", movieImage);
            // set Fragmentclass Arguments
        fragobj.setArguments(bundle);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

//    public String getDataString() {
//        Intent intent = getIntent();
//        String movieImage = intent.getStringExtra(MainActivityFragment.EXTRA_MOVIEURL);
//        return movieImage;
//    }

}
