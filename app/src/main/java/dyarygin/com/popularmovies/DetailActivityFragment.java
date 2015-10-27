package dyarygin.com.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        DetailActivity detailActivity = (DetailActivity) getActivity();
        String movieImage = detailActivity.getMovieImage();

        Picasso.with(getContext())
                .load(movieImage)
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
