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

    Bundle bundle;
    String myValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        Picasso.with(getContext())
                .load(R.drawable.movie_image)
                        //TODO: Add placeholder and error resources
                        // .placeholder(R.drawable.)
                        //.error(R.drawable.)
                .noFade().resize(1000,1000)
                .centerCrop()
                .into(imageView);
        imageView.setAdjustViewBounds(true);
//
//        Toast.makeText(getContext(), "From Activity: " + myValue,
//                Toast.LENGTH_SHORT).show();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        bundle = getArguments();
//        myValue = this.bundle.getString("message");
//        Log.e("VALUE IS:", myValue);
    }
}
