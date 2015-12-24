package dyarygin.com.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {

    private List<TrailersInfo> trailersList;

    public TrailersAdapter(List<TrailersInfo> trailersList){
        this.trailersList = trailersList;
    }

    @Override
    public int getItemCount() {
        return trailersList.size();
    }

    @Override
    public void onBindViewHolder(TrailersViewHolder trailersViewHolder, int i ){
        TrailersInfo ti = trailersList.get(i);
        trailersViewHolder.vTitle.setText(ti.title);
        trailersViewHolder.vDescription.setText(ti.description);
    }

    @Override
    public TrailersViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_trailers,viewGroup,false);
        return new TrailersViewHolder(itemView);
    }

    public static class TrailersViewHolder extends RecyclerView.ViewHolder{
        protected TextView vTitle;
        protected TextView vDescription;

        public TrailersViewHolder(View v) {
            super(v);
            vTitle = (TextView) v.findViewById(R.id.trailerTitle);
            vDescription = (TextView) v.findViewById(R.id.trailerDescription);
        }
    }
}
