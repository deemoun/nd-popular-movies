package dyarygin.com.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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

public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    public final static String EXTRA_MOVIEID = "dyarygin.com.popularmovies.MOVIEID";
    public final static String EXTRA_MOVIEIMAGE = "dyarygin.com.popularmovies.MOVIEIMAGE";
    public final static String EXTRA_MOVIEBACKDROPPATH = "dyarygin.com.popularmovies.MOVIEBACKDROPPATH";
    public final static String EXTRA_MOVIEVOTE = "dyarygin.com.popularmovies.MOVIEVOTE";
    public final static String EXTRA_MOVIERELEASEDATE = "dyarygin.com.popularmovies.MOVIERELEASEDATE";
    public final static String EXTRA_MOVIEORIGINALTITLE = "dyarygin.com.popularmovies.MOVIEORIGINALTITLE";
    public final static String EXTRA_MOVIEOVERVIEW = "dyarygin.com.popularmovies.MOVIEOVERVIEW";

    // Image size format used for the displaying images
    public final static String IMAGE_FORMAT = "w185";

    private static List<String> movieIdList = new ArrayList<>();
    private static List<String> movieImageList = new ArrayList<>();
    private static List<String> movieBackdropPathList = new ArrayList<>();
    private static List<String> movieOriginalTitleList = new ArrayList<>();
    private static List<String> movieOverviewList = new ArrayList<>();
    private static List<String> movieVoteAverage = new ArrayList<>();
    private static List<String> movieReleaseDate = new ArrayList<>();
    private static Realm mRealm;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(getActivity().getApplicationContext());
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
        final MainActivity mainActivity = (MainActivity) getActivity();
        setRetainInstance(true);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        // Getting current sort value from SharedPreference
        String sortOrderValue = sharedPref.getString(getString(R.string.pref_key_sort_order), "popularity.desc");
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
    mRealm.close();
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
        movieIdList.clear();
        movieImageList.clear();
        movieBackdropPathList.clear();
        movieOriginalTitleList.clear();
        movieVoteAverage.clear();
        movieReleaseDate.clear();
        movieOverviewList.clear();
        // Writing current value to Share Pref
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_key_sort_order), sortOrder);
        editor.commit();
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.execute(sortOrder, imgSize);
    }

    public void updateMovies(){

        // Get the data from Realm database

        mRealm.refresh();

        RealmResults<Movie> results = mRealm.where(Movie.class).equalTo("isFavorite", true).findAll();
        final String[] posterImage = new String[results.size()];
        final String[] movieId = new String[results.size()];
        final String[] movieOriginalTitle = new String[results.size()];
        final String[] voteAverage = new String[results.size()];
        final String[] movieReleaseDate = new String[results.size()];
        final String[] movieOverview = new String[results.size()];

        // Add results to an Array
        for (int i = 0; i < results.size(); i++) {
            posterImage[i] = results.get(i).getPosterImage();
            movieId[i] = results.get(i).getMovieId();
            movieOriginalTitle[i] = results.get(i).getMovieOriginalTitle();
            voteAverage[i] = results.get(i).getVoteAverage();
            movieReleaseDate[i] = results.get(i).getMovieReleaseDate();
            movieOverview[i] = results.get(i).getMovieOverview();
        }

        View view = getView();
        if (view != null) {
            GridView gridview = ButterKnife.findById(view, R.id.movies_grid);
            ImageAdapter imageAdapter = new ImageAdapter(getActivity().getApplicationContext(), posterImage, 555, 834);
            gridview.setAdapter(imageAdapter);

            // Setting onClickListener on GridView
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handling of the click
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(EXTRA_MOVIEIMAGE, posterImage[position]);
                    intent.putExtra(EXTRA_MOVIEVOTE, voteAverage[position]);
                    intent.putExtra(EXTRA_MOVIERELEASEDATE, movieReleaseDate[position]);
                    intent.putExtra(EXTRA_MOVIEOVERVIEW, movieOverview[position]);
                    intent.putExtra(EXTRA_MOVIEORIGINALTITLE, movieOriginalTitle[position]);
                    intent.putExtra(EXTRA_MOVIEID, movieId[position]);
                    startActivity(intent);

                }
            });
        }
    }

    public void setGridView() {
        final String[] imgArray = movieImageList.toArray(new String[movieImageList.size()]);
        final String[] backdropPath = movieBackdropPathList.toArray(new String[movieBackdropPathList.size()]);
        final String[] voteArray = movieVoteAverage.toArray(new String[movieVoteAverage.size()]);
        final String[] releaseDate = movieReleaseDate.toArray(new String[movieReleaseDate.size()]);
        final String[] overview = movieOverviewList.toArray(new String[movieOverviewList.size()]);
        final String[] movId = movieIdList.toArray(new String[movieIdList.size()]);
        final String[] title = movieOriginalTitleList.toArray(new String[movieOriginalTitleList.size()]);

        View view = getView();
        if (view != null) {
            GridView gridview = ButterKnife.findById(view, R.id.movies_grid);
            ImageAdapter imageAdapter = new ImageAdapter(getActivity().getApplicationContext(), imgArray, 555, 834);
            gridview.setAdapter(imageAdapter);

            // Setting onClickListener on GridView
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(EXTRA_MOVIEIMAGE, imgArray[position]);
                    intent.putExtra(EXTRA_MOVIEBACKDROPPATH, backdropPath[position]);
                    intent.putExtra(EXTRA_MOVIEVOTE, voteArray[position]);
                    intent.putExtra(EXTRA_MOVIERELEASEDATE, releaseDate[position]);
                    intent.putExtra(EXTRA_MOVIEOVERVIEW, overview[position]);
                    intent.putExtra(EXTRA_MOVIEORIGINALTITLE, title[position]);
                    intent.putExtra(EXTRA_MOVIEID, movId[position]);
                    startActivity(intent);

                }
            });
        }
    }

                public class FetchMovieTask extends AsyncTask<String, Void, List<String>> {

                    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

                    private List<String> getMovieDataFromJson(String movieJsonStr, String imageSize) throws JSONException {
                        // Getting the root "results" array
                        JSONObject movieObject = new JSONObject(movieJsonStr);

                        JSONArray movieArray = movieObject.getJSONArray("results");

                        // Base Url for the TMDB images
                        final String ImageBaseUrl = "http://image.tmdb.org/t/p/" + imageSize;


                        for (int i = 0; i < movieArray.length(); i++) {
                            JSONObject movieTitle = movieArray.getJSONObject(i);

                            movieIdList.add(movieTitle.getString("id"));
                            movieImageList.add(ImageBaseUrl + movieTitle.getString("poster_path"));
                            movieBackdropPathList.add(ImageBaseUrl + movieTitle.getString("backdrop_path"));
                            movieOriginalTitleList.add(movieTitle.getString("original_title"));
                            movieOverviewList.add(movieTitle.getString("overview"));
                            movieVoteAverage.add(movieTitle.getString("vote_average"));
                            movieReleaseDate.add(movieTitle.getString("release_date"));
                        }
                        return movieImageList;
                    }

                    @Override
                    protected List<String> doInBackground(String... params) {

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
                            return getMovieDataFromJson(movieDataStr, params[1]);
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                            e.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(List<String> result) {
                        if (result != null) {
                            setGridView();
                        }
                    }
                }
        }