package dyarygin.com.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivityFragment extends Fragment {
    public final static String EXTRA_MOVIEIMAGE = "dyarygin.com.popularmovies.MOVIEIMAGE";
    public final static String EXTRA_MOVIEBACKDROPPATH = "dyarygin.com.popularmovies.MOVIEBACKDROPPATH";
    public final static String EXTRA_MOVIEVOTE = "dyarygin.com.popularmovies.MOVIEVOTE";
    public final static String EXTRA_MOVIERELEASEDATE = "dyarygin.com.popularmovies.MOVIERELEASEDATE";
    public final static String EXTRA_MOVIEORIGINALTITLE = "dyarygin.com.popularmovies.MOVIEORIGINALTITLE";
    public final static String EXTRA_MOVIEOVERVIEW = "dyarygin.com.popularmovies.MOVIEOVERVIEW";

    public static List<String> movieIdList = new ArrayList<>();
    public static List<String> movieImageList = new ArrayList<>();
    public static List<String> movieBackdropPathList = new ArrayList<>();
    public static List<String> movieOriginalTitleList = new ArrayList<>();
    public static List<String> movieOverviewList = new ArrayList<>();
    public static List<String> movieVoteAverage = new ArrayList<>();
    public static List<String> movieReleaseDate = new ArrayList<>();


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.show_most_popular: updateMovies("popularity.desc", "w185");
                break;
            case R.id.show_highest_rated: updateMovies("vote_average.desc", "w185");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        updateMovies("popularity.desc", "w185");
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void updateMovies(String sortOrder, String imgSize){
        movieImageList.clear();
        movieIdList.clear();
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.execute(sortOrder, imgSize);
    }

    void errorWhileRetrieving() {
        Toast.makeText(getContext(), "Error while retrieving data", Toast.LENGTH_LONG).show();
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
                        .appendQueryParameter("api_key", Config.DBAPIKEY)
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
                return getMovieDataFromJson(movieDataStr,params[1]);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> result) {

            GridView gridview = (GridView) getView().findViewById(R.id.movies_grid);


            if (result != null) {
                final String[] imgArray = result.toArray(new String[result.size()]);
                final String[] backdropPath = movieBackdropPathList.toArray(new String[movieBackdropPathList.size()]);
                final String[] voteArray = movieVoteAverage.toArray(new String[movieVoteAverage.size()]);
                final String[] releaseDate = movieReleaseDate.toArray(new String[movieReleaseDate.size()]);
                final String[] overview = movieOverviewList.toArray(new String[movieOverviewList.size()]);
                final String[] title = movieOriginalTitleList.toArray(new String[movieOriginalTitleList.size()]);
                gridview.setAdapter(new ImageAdapter(getActivity(), imgArray));


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
                        startActivity(intent);
                    }
                });
            } else {
                errorWhileRetrieving();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}




