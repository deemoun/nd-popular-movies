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
        TextView movieOriginalTitleTextView = (TextView) view.findViewById(R.id.movieOriginalTitleTextView);
        ImageView posterImageView = (ImageView) view.findViewById(R.id.posterImageView);
        TextView voteAverageTextView = (TextView) view.findViewById(R.id.voteAverageTextView);
        TextView movieReleaseDateTextView = (TextView) view.findViewById(R.id.movieReleaseDateTextView);
        TextView movieOverviewTextView = (TextView) view.findViewById(R.id.movieOverviewTextView);

        // Setting up the views from intent
        DetailActivity detailActivity = (DetailActivity) getActivity();
        String posterImage = detailActivity.getMovieImage();
        String movieBackdropPath = detailActivity.getMovieBackdropPath();
        String voteAverage = detailActivity.getVoteAverage();
        String releaseDate = detailActivity.getReleaseDate();
        String overview = detailActivity.getMovieOverview();
        String title = detailActivity.getOriginalTitle();

        movieOriginalTitleTextView.setText(title);
        voteAverageTextView.setText(getString(R.string.vote_average) + ": " + voteAverage);
        movieReleaseDateTextView.setText(releaseDate);
        movieOverviewTextView.setText(overview);


        Picasso.with(getContext())
                .load(posterImage)
                        //TODO: Add placeholder and error resources
                        // .placeholder(R.drawable.)
                        //.error(R.drawable.)
                .noFade().resize(555,834)
                .centerCrop()
                .into(posterImageView);

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
