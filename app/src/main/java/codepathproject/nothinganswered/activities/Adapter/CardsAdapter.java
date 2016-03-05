package codepathproject.nothinganswered.activities.Adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.activities.Model.Card;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private List<Card> mCards;
    public CardsAdapter (List<Card> cards)
    {
        mCards = cards;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView cardsImageUrl;
        public TextView cardsTitle;


        public ViewHolder(View itemView) {
            super(itemView);
            cardsImageUrl = (ImageView) itemView.findViewById(R.id.imageView);
            cardsTitle = (TextView) itemView.findViewById(R.id.card_title);

        }
    }
    public CardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardsAdapter.ViewHolder holder, int position) {
        Card card = mCards.get(position);

        TextView textView = holder.cardsTitle;
        ImageView imageView = holder.cardsImageUrl;



    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

}
