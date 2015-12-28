package dyarygin.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {

    private List<TrailersInfo> trailersList;
    private Context mContext;

    public TrailersAdapter(List<TrailersInfo> trailersList, Context context){
        this.trailersList = trailersList;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return trailersList.size();
    }

    @Override
    public void onBindViewHolder(TrailersViewHolder trailersViewHolder, int i ){
        final TrailersInfo ti = trailersList.get(i);
        trailersViewHolder.context = mContext;
        trailersViewHolder.currentValue = ti;
        trailersViewHolder.vCardName.setText(ti.cardname);
    }

    @Override
    public TrailersViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_trailers,viewGroup,false);
        return new TrailersViewHolder(itemView);
    }

    public static class TrailersViewHolder extends RecyclerView.ViewHolder{
        protected TextView vCardName;
        public TrailersInfo currentValue;
        protected Context context;

        public TrailersViewHolder(View v) {
            super(v);
            vCardName = (TextView) v.findViewById(R.id.trailerCardName);
            vCardName.setOnClickListener(new View.OnClickListener()       {
                @Override
            public void onClick(View v) {
                    Utils.Logger("Youtube link is: " + currentValue.title);
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentValue.title)));
                }});
        }
    }
}
