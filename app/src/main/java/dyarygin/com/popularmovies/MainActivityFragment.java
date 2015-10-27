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
    public final static String EXTRA_MOVIEURL = "dyarygin.com.popularmoview.MOVIEURL";

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
            case R.id.show_highest_rated: updateMovies("vote_count.desc", "w185");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        updateMovies("popularity.desc","w185");
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void updateMovies(String sortOrder, String imgSize){
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.execute(sortOrder, imgSize);
    }

    void errorWhileRetrieving() {
        Toast.makeText(getContext(), "Error while retrieving data", Toast.LENGTH_LONG).show();
    }

    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private String[] getMovieDataFromJson(String movieJsonStr, String imageSize) throws JSONException {

            // Getting the root "results" array
            JSONObject movieObject = new JSONObject(movieJsonStr);

            JSONArray movieArray = movieObject.getJSONArray("results");

            // Base Url for the images
            final String ImageBaseUrl = "http://image.tmdb.org/t/p/" + imageSize;
            String[] resultStr = new String[movieArray.length()];

            for (int i = 0; i < movieArray.length(); i++) {

                JSONObject movieTitle = movieArray.getJSONObject(i);

                String movieImageValue = movieTitle.getString("poster_path");
                String movieId = movieTitle.getString("id");

                resultStr[i] = ImageBaseUrl + movieImageValue;
                System.out.println("Movie ID is " + movieId);
            }
            return resultStr;
        }

        @Override
        protected String[] doInBackground(String... params) {


            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
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
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
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
        protected void onPostExecute(String[] result) {
            List<String> movieArrayList = new ArrayList<String>();
            GridView gridview = (GridView) getView().findViewById(R.id.movies_grid);

            if (result != null) {
                for (String movieStr : result) {
                    movieArrayList.add(movieStr);
                }
                final String[] strarray = movieArrayList.toArray(new String[result.length]);
                ImageAdapter imageAdapter =  new ImageAdapter(getActivity(), strarray);

                // Setting the Adapter and onCick listener on it

                gridview.setAdapter(imageAdapter);

                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra(EXTRA_MOVIEURL, strarray[position]);
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




