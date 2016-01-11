package dyarygin.com.popularmovies;

import android.app.Application;
import android.util.Log;

public class Utils extends Application {

    //Intent IDs
    public final static String EXTRA_MOVIEID = "dyarygin.com.popularmovies.MOVIEID";

    public static void Logger(String logStatement){
        final String LOG_TAG = "Popular Movies";
        Log.v(LOG_TAG, logStatement);
    }
 }
