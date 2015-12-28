package dyarygin.com.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<ReviewInfo> reviewList;

    public ReviewAdapter(List<ReviewInfo> reviewList){
        this.reviewList = reviewList;
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder reviewViewHolder, int i ){
        final ReviewInfo ti = reviewList.get(i);
        reviewViewHolder.vCardName.setText(ti.reviewContent);
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_reviews,viewGroup,false);
        return new ReviewViewHolder(itemView);
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder{
        protected TextView vCardName;

        public ReviewViewHolder(View v) {
            super(v);
            vCardName = (TextView) v.findViewById(R.id.reviewCardName);
        }
    }
}