package dyarygin.com.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private String[] adapterArray;
    private int adapterWidth;
    private int adapterHeight;

    public ImageAdapter(Context c, String[] a, int w, int h) {
        mContext = c;
        adapterArray = a;
        adapterWidth = w;
        adapterHeight = h;
    }

    public int getCount() {
        return adapterArray.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }

    // Use Picasso to show images

        Picasso.with(mContext)
                .load(adapterArray[position])
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.no_movie_image)
                .noFade().resize(adapterWidth, adapterHeight)
                .centerCrop()
                .into(imageView);
        imageView.setAdjustViewBounds(true);

        return imageView;

    }
}