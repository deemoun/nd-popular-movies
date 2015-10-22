package dyarygin.com.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private String[] adapterString;

    public ImageAdapter(Context c, String[] a) {
        mContext = c;
        adapterString = a;
    }

    public int getCount() {
        return adapterString.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext)
                .load(adapterString[position])
                        //TODO: Add placeholder and error resources
                        // .placeholder(R.drawable.)
                        //.error(R.drawable.)
                .noFade().resize(400,650)
                .centerCrop()
                .into(imageView);
        imageView.setAdjustViewBounds(true);


        return imageView;

    }
}