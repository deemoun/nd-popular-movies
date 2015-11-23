package dyarygin.com.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class DetailActivityFragment extends Fragment {

    private Context context;
    private MoviesDataSource dataSource;

    public DetailActivityFragment() {
    }

    private static List<String> movieTrailerList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Log.v("onCreate", "Executing AsyncTask");
        FetchTrailerTask ft = new FetchTrailerTask();
        ft.execute();
    }

    // Adding FetchTrailerTask

    public class FetchTrailerTask extends AsyncTask<String, Void, List<String>> {

        private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

        private List<String> getTrailerDataFromJson(String trailerJsonStr) throws JSONException {
            // Getting the root "results" array
            JSONObject trailerObject = new JSONObject(trailerJsonStr);

            JSONArray trailerArray = trailerObject.getJSONArray("results");

            // Base Url for the Trailers
            final String YoutubeBaseUrl = "https://www.youtube.com/watch?v=";


            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                if(trailer.getString("site").contentEquals("YouTube")) {
                    movieTrailerList.add(YoutubeBaseUrl + trailer.getString("key"));
                }
                Log.v(LOG_TAG,"Youtube URL IS " + movieTrailerList.get(i));

            }
            return movieTrailerList;
        }

        @Override
        protected List<String> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String trailerDataStr = null;

            try {
                // Construct the URL
                final DetailActivity detailActivity = (DetailActivity) getActivity();
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + detailActivity.getMovieId() + "/videos";

                Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", Config.DBAPIKEY)
                        .build();

                URL url = new URL(buildUri.toString());
                Log.e(LOG_TAG, "Trailer url is " + url);


                // Create the request to TMDB
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                trailerDataStr = buffer.toString();
                Log.e(LOG_TAG, "TRAILER STREAM IS " + trailerDataStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getTrailerDataFromJson(trailerDataStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(List<String> result) {
            if (result != null) {
                for (int i = 0; i < movieTrailerList.size(); i++) {
                    Log.v(LOG_TAG, "RESULT IS" + movieTrailerList);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // Finding views
        TextView movieOriginalTitleTextView = ButterKnife.findById(view, R.id.movieOriginalTitleTextView);
        ImageView posterImageView = ButterKnife.findById(view, R.id.posterImageView);
        TextView voteAverageTextView = ButterKnife.findById(view, R.id.voteAverageTextView);
        TextView movieReleaseDateTextView = ButterKnife.findById(view, R.id.movieReleaseDateTextView);
        TextView movieOverviewTextView = ButterKnife.findById(view,R.id.movieOverviewTextView);
        Button favoriteButton = ButterKnife.findById(view, R.id.favoriteButton);

        // Setting up the views from intent
        final DetailActivity detailActivity = (DetailActivity) getActivity();
        String posterImage = detailActivity.getMovieImage();
        String movieBackdropPath = detailActivity.getMovieBackdropPath();
        String voteAverage = detailActivity.getVoteAverage();
        String releaseDate = detailActivity.getReleaseDate();
        String overview = detailActivity.getMovieOverview();
        String title = detailActivity.getOriginalTitle();

        movieOriginalTitleTextView.setText(title);
        voteAverageTextView.setText(voteAverage);
        movieReleaseDateTextView.setText(releaseDate);
        if (overview.contentEquals("null")) {
            movieOverviewTextView.setText(getString(R.string.no_movie_overview));
        } else {
            movieOverviewTextView.setText(overview);
        }

        //Creating DataSource object and adding it to database
        dataSource = new MoviesDataSource(getContext());
        dataSource.open();

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSource.createMovie(detailActivity.getOriginalTitle());
            }
        });

            Picasso.with(context)
                    .load(posterImage)
                    .placeholder(R.drawable.movie_placeholder)
                    .error(R.drawable.no_movie_image)
                    .noFade().resize(555, 834)
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
        dataSource.close();
        movieTrailerList.clear();
    }

    @Override
    public  void onStop(){
        super.onStop();
        movieTrailerList.clear();
    }
}
