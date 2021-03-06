package dyarygin.com.popularmovies;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class MainActivityFragment extends Fragment {

    private static Realm mRealm;
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private static View view;
    // Image size format used for the displaying images
    public final static String IMAGE_FORMAT = "w185";
    movieSelectorListener mCallback;

    public MainActivityFragment() {
    }

    public interface movieSelectorListener {
        void onMovieSelected(String position);
    }

    public View getCurrentView(){
        return view = getView();
    }

    public Realm getRealmInstance(){
        return mRealm = Realm.getInstance(getActivity().getApplicationContext());
    }

    public String getSortOrderSharedPrefs(){
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(getString(R.string.pref_key_sort_order), "popularity.desc");
    }

    public void setSortOrderSharedPrefs(String sortOrder) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_key_sort_order), sortOrder);
        editor.commit();
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            /*
             * Toast.makeText(getActivity(), "No Internet connection!",
             * Toast.LENGTH_LONG).show();
             */
            return false;
        }
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (movieSelectorListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onMovieSelected");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Clearing a menu to not have an issue with duplicated buttons
        menu.clear();
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.show_most_popular: updateMovies("popularity.desc", IMAGE_FORMAT);
                break;
            case R.id.show_highest_rated: updateMovies("vote_average.desc", IMAGE_FORMAT);
                break;
            case R.id.show_favorites: updateMovies();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_main, container, false);
        setRetainInstance(true);
        // Getting current sort value from SharedPreference

        String sortOrderValue = getSortOrderSharedPrefs();
        updateMovies(sortOrderValue, IMAGE_FORMAT);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void updateMovies(String sortOrder, String imgSize){
        // Writing current value to Share Pref
        setSortOrderSharedPrefs(sortOrder);
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.execute(sortOrder, imgSize);
    }

    public void updateMovies(){

        // Get the data from Realm database
        getRealmInstance();

        RealmResults<Movie> results = mRealm.where(Movie.class)
                .equalTo("isFavorite", true)
                .findAll();
        final String[] posterImage = new String[results.size()];
        final String[] movieId = new String[results.size()];

        if(results.size() != 0) {
            // Add results to an Array
            for (int i = 0; i < results.size(); i++) {
                posterImage[i] = results.get(i).getPosterImage();
                movieId[i] = results.get(i).getMovieId();
            }

            if (getCurrentView() != null) {
                GridView gridview = ButterKnife.findById(view, R.id.movies_grid);
                ImageAdapter imageAdapter = new ImageAdapter(getActivity().getApplicationContext(), posterImage, 555, 834);
                gridview.setAdapter(imageAdapter);

                // Setting onClickListener on GridView
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Handling of the click
                        mCallback.onMovieSelected(movieId[position]);
                    }
                });
            }
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "No favorite movies yet", Toast.LENGTH_SHORT).show();
        }
      }

    public void setGridView() {
        getRealmInstance();
        RealmResults<Movie> results = mRealm.where(Movie.class)
                .equalTo("sortOrder", getSortOrderSharedPrefs())
                .findAll();
        final String[] posterImage = new String[results.size()];
        final String[] movieId = new String[results.size()];

        for (int i = 0; i < results.size(); i++){
            posterImage[i] = results.get(i).getPosterImage();
            movieId[i] = results.get(i).getMovieId();
        }

        if (getCurrentView() != null) {
            GridView gridview = ButterKnife.findById(view, R.id.movies_grid);
            ImageAdapter imageAdapter = new ImageAdapter(getActivity().getApplicationContext(), posterImage, 555, 834);
            gridview.setAdapter(imageAdapter);

            // Setting onClickListener on GridView
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Utils.Logger("Making callback to activity");
                    mCallback.onMovieSelected(movieId[position]);
                }
            });
        }
    }

                public class FetchMovieTask extends AsyncTask<String, Void, String> {

                    private void getMovieDataFromJson(String movieJsonStr, String imageSize) throws JSONException {
                        // Getting the root "results" array
                        getRealmInstance();
                        JSONObject movieObject = new JSONObject(movieJsonStr);

                        JSONArray movieArray = movieObject.getJSONArray("results");
                        // Base Url for the TMDB images
                        final String ImageBaseUrl = "http://image.tmdb.org/t/p/" + imageSize;
                            for (int i = 0; i < movieArray.length(); i++) {
                                JSONObject movieTitle = movieArray.getJSONObject(i);
                                mRealm.beginTransaction();
                                Movie movieDB = new Movie();
                                movieDB.setMovieId(movieTitle.getString("id"));
                                movieDB.setPosterImage(ImageBaseUrl + movieTitle.getString("poster_path"));
                                movieDB.setMovieOriginalTitle(movieTitle.getString("original_title"));
                                movieDB.setMovieOverview(movieTitle.getString("overview"));
                                movieDB.setVoteAverage(movieTitle.getString("vote_average"));
                                movieDB.setMovieReleaseDate(movieTitle.getString("release_date"));
                                movieDB.setSortOrder(getSortOrderSharedPrefs());
                                try {
                                    mRealm.copyToRealm(movieDB);
                                    mRealm.commitTransaction();
                                } catch (RealmPrimaryKeyConstraintException e) {
                                    Utils.Logger("Realm value already exists!");
                                    mRealm.commitTransaction();
                                }
                            }
                    }

                    @Override
                    protected String doInBackground(String... params) {

                        if (params.length == 0) {
                            return null;
                        }

                        HttpURLConnection urlConnection = null;
                        BufferedReader reader = null;

                        // Will contain the raw JSON response as a string.
                        String movieDataStr = null;

                        try {
                            // Construct the URL
                            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";

                            Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                                    .appendQueryParameter("sort_by", params[0])
                                            // NOTE: APIKEY should be added to Config class
                                    .appendQueryParameter("api_key", Config.DBAPIKEY)
                                    .appendQueryParameter("page", "1")
                                    .build();

                            URL url = new URL(buildUri.toString());
                            Log.e(LOG_TAG, "TMDB url is " + url);


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
                            movieDataStr = buffer.toString();
                            Log.e(LOG_TAG, "DATA STREAM IS " + movieDataStr);

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
                            getMovieDataFromJson(movieDataStr, params[1]);
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                            e.printStackTrace();
                        }
                        return movieDataStr;
                    }
                    @Override
                    protected void onPostExecute(String result) {
                        if (result != null) {
                            Utils.Logger("Setting Grid View");
                            setGridView();
                        }
                    }
                }
        }