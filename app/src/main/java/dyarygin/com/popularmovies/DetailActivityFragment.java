package dyarygin.com.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // Finding views
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView voteAverageTextView = (TextView) view.findViewById(R.id.voteAverageTextView);
        TextView movieReleaseDateTextView = (TextView) view.findViewById(R.id.movieReleaseDateTextView);
        TextView movieOverviewTextView = (TextView) view.findViewById(R.id.movieOverviewTextView);
        TextView movieOriginalTitleTextView = (TextView) view.findViewById(R.id.movieOriginalTitleTextView);

        // Setting up the views from intent
        DetailActivity detailActivity = (DetailActivity) getActivity();
        String posterImage = detailActivity.getMovieImage();
        String movieBackdropPath = detailActivity.getMovieBackdropPath();
        String voteAverage = detailActivity.getVoteAverage();
        String releaseDate = detailActivity.getReleaseDate();
        String overview = detailActivity.getMovieOverview();
        String title = detailActivity.getOriginalTitle();

        voteAverageTextView.setText(R.string.vote_average + " " + voteAverage);
        movieReleaseDateTextView.setText(R.string.release_date + " " + releaseDate);
        movieOriginalTitleTextView.setText(R.string.title + " " + title);
        movieOverviewTextView.setText(R.string.overview + " " + overview);


        Picasso.with(getContext())
                .load(posterImage)
                        //TODO: Add placeholder and error resources
                        // .placeholder(R.drawable.)
                        //.error(R.drawable.)
                .noFade().resize(400,500)
                .centerCrop()
                .into(imageView);
        imageView.setAdjustViewBounds(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
