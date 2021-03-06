package dyarygin.com.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import io.realm.Realm;
import io.realm.RealmResults;

public class DetailActivityFragment extends Fragment {

    private static Realm mRealm;
    private Context context;
    private List<String> movieTrailerList = new ArrayList<>();
    private List<String> movieReviewListReview = new ArrayList<>();
    private List<String> movieReviewListAuthor = new ArrayList<>();

    public DetailActivityFragment() {
    }

    private String getMovieId() {

        Bundle bundle = this.getArguments();

        if(getActivity().getIntent().getStringExtra(Utils.EXTRA_MOVIEID) != null){
            return getActivity().getIntent().getStringExtra(Utils.EXTRA_MOVIEID);
        } else {
            if (getArguments() != null) {
                return bundle.getString(Utils.EXTRA_MOVIEID, "140607");
            } else {
                // Returning one of the movies just in case
                return "140607";
            }
        }
    }

    public Realm getRealmInstance(){
        return mRealm = Realm.getInstance(getActivity().getApplicationContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        context = getActivity().getApplicationContext();
    }

    public String getSortOrderSharedPrefs(){
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(getString(R.string.pref_key_sort_order), "popularity.desc");
    }

    // Adding FetchTrailerTask

    public class FetchTrailerTask extends AsyncTask<String, Void, String> {

        private final String TRAILER_LOG_TAG = FetchTrailerTask.class.getSimpleName();

        private void getTrailerDataFromJson(String trailerJsonStr) throws JSONException {
            // Getting the root "results" array
            JSONObject trailerObject = new JSONObject(trailerJsonStr);

            JSONArray trailerArray = trailerObject.getJSONArray("results");

            // Base Url for the TrailersInfo
            final String YoutubeBaseUrl = "https://www.youtube.com/watch?v=";


            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                if(trailer.getString("site").contentEquals("YouTube")) {
                    movieTrailerList.add(i, YoutubeBaseUrl + trailer.getString("key"));
                }
                Log.v(TRAILER_LOG_TAG, "Youtube URL IS " + movieTrailerList.get(i));

            }
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String trailerDataStr = null;

            try {
                // Construct the URL
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + getMovieId() + "/videos";

                Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", Config.DBAPIKEY)
                        .build();

                URL url = new URL(buildUri.toString());
                Log.e(TRAILER_LOG_TAG, "Trailer url is " + url);


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
                Log.e(TRAILER_LOG_TAG, "TRAILER STREAM IS " + trailerDataStr);

            } catch (IOException e) {
                Log.e(TRAILER_LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TRAILER_LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                getTrailerDataFromJson(trailerDataStr);
            } catch (JSONException e) {
                Log.e(TRAILER_LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v(TRAILER_LOG_TAG, "RESULT IS" + movieTrailerList);
            RecyclerView recList = (RecyclerView) getView().findViewById(R.id.trailerCard);
            recList.setHasFixedSize(true);
            LinearLayoutManager llm = new MyLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
            recList.setLayoutManager(llm);
            TrailersAdapter ta = new TrailersAdapter(createYoutubeList(),getContext());
            recList.setAdapter(ta);
        }
    }

    // Adding FetchReviewsTask
    private final String REVIEW_LOG_TAG = "FetchReviewTask";

    public class FetchReviewTask extends AsyncTask<String, Void, String> {


        private void getReviewDataFromJson(String reviewJsonStr) throws JSONException {
            // Getting the root "results" array
            JSONObject reviewObject = new JSONObject(reviewJsonStr);

            JSONArray reviewArray = reviewObject.getJSONArray("results");


            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                movieReviewListReview.add(i, review.getString("content"));
                movieReviewListAuthor.add(i,review.getString("author"));
            }
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String reviewDataStr = null;

            try {
                // Construct the URL
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + getMovieId() + "/reviews";

                Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", Config.DBAPIKEY)
                        .build();

                URL url = new URL(buildUri.toString());
                Log.e(REVIEW_LOG_TAG, "Review url is " + url);


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
                reviewDataStr = buffer.toString();
                Log.e(REVIEW_LOG_TAG, "REVIEW STREAM IS " + reviewDataStr);

            } catch (IOException e) {
                Log.e(REVIEW_LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(REVIEW_LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                getReviewDataFromJson(reviewDataStr);
            } catch (JSONException e) {
                Log.e(REVIEW_LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String result) {
            RecyclerView recListReviews = (RecyclerView) getView().findViewById(R.id.reviewCard);
            recListReviews.setHasFixedSize(true);
            LinearLayoutManager llm = new MyLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
            recListReviews.setLayoutManager(llm);
            ReviewAdapter ra = new ReviewAdapter(createReviewList());
            recListReviews.setAdapter(ra);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        getRealmInstance();

        // Finding views
        TextView movieOriginalTitleTextView = ButterKnife.findById(view, R.id.movieOriginalTitleTextView);
        ImageView posterImageView = ButterKnife.findById(view, R.id.posterImageView);
        TextView voteAverageTextView = ButterKnife.findById(view, R.id.voteAverageTextView);
        TextView movieReleaseDateTextView = ButterKnife.findById(view, R.id.movieReleaseDateTextView);
        TextView movieOverviewTextView = ButterKnife.findById(view, R.id.movieOverviewTextView);
        final Button favoriteButton = ButterKnife.findById(view, R.id.favoriteButton);

        // Setting up the views from intent

        final String movieId = getMovieId();

//         Getting data from Realm
        final RealmResults<Movie> results = mRealm.where(Movie.class).equalTo("movieId", movieId).findAll();

        final String posterImage = results.get(0).getPosterImage();
        final String voteAverage = results.get(0).getVoteAverage();
        final String releaseDate = results.get(0).getMovieReleaseDate();
        final String overview = results.get(0).getMovieOverview();
        final String title = results.get(0).getMovieOriginalTitle();
        final boolean isFavorite = results.get(0).getIsFavorite();

        if(isFavorite){
            favoriteButton.setText(getString(R.string.remove_from_favorite));
        } else {
            favoriteButton.setText(getString(R.string.mark_as_fav_movie));
        }

        movieOriginalTitleTextView.setText(title);
        voteAverageTextView.setText(voteAverage);
        movieReleaseDateTextView.setText(releaseDate);
        if (overview.contentEquals("null")) {
            movieOverviewTextView.setText(getString(R.string.no_movie_overview));
        } else {
            movieOverviewTextView.setText(overview);
        }

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRealmInstance();
                Movie favModel = new Movie();
                RealmResults<Movie> resultsFav = mRealm.where(Movie.class)
                        .equalTo("movieId", movieId)
                        .findAll();
                mRealm.beginTransaction();
                favModel.setMovieId(movieId);
                favModel.setPosterImage(posterImage);
                favModel.setMovieOriginalTitle(title);
                favModel.setMovieOverview(overview);
                favModel.setVoteAverage(voteAverage);
                favModel.setMovieReleaseDate(releaseDate);
                favModel.setSortOrder(getSortOrderSharedPrefs());
                if(resultsFav.get(0).getIsFavorite()){
                    favModel.setIsFavorite(false);
                    favoriteButton.setText(getString(R.string.mark_as_fav_movie));
                } else {
                    favModel.setIsFavorite(true);
                    favoriteButton.setText(getString(R.string.remove_from_favorite));
                }
                mRealm.copyToRealmOrUpdate(favModel);
                mRealm.commitTransaction();
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

    private List<TrailersInfo> createYoutubeList() {

        List<TrailersInfo> result = new ArrayList<>();
        Utils.Logger("Executing Trailer AsyncTask");
        for (int i=0; i < movieTrailerList.size(); i++) {
            TrailersInfo ti = new TrailersInfo();
            ti.title = movieTrailerList.get(i);
            ti.cardname = TrailersInfo.Trailer_PREFIX + (i+1);
            result.add(ti);
        }

        return result;
    }

    private List<ReviewInfo> createReviewList() {

        List<ReviewInfo> result = new ArrayList<>();
        Utils.Logger("Executing Review AsyncTask");
        for (int i=0; i < movieReviewListReview.size(); i++) {
            ReviewInfo ri = new ReviewInfo();
            ri.reviewAuthor = movieReviewListAuthor.get(i);
            ri.reviewContent = movieReviewListReview.get(i);
            result.add(ri);
        }
        return result;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Executing FetchTrailer task
        FetchTrailerTask ftT = new FetchTrailerTask();
        ftT.execute();

        // Executing FetchReview task
        FetchReviewTask ftR = new FetchReviewTask();
        ftR.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRealm.isClosed()){
            // Already closed
        }
        else {
            mRealm.close();
        }
    }

    @Override
    public  void onStop(){
        super.onStop();
        if (mRealm.isClosed()){
            // Already closed
        }
        else {
            mRealm.close();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRealm.isClosed()){
            // Already closed
        }
        else {
            mRealm.close();
        }
    }
}