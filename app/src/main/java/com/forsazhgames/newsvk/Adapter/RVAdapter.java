package com.forsazhgames.newsvk.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.forsazhgames.newsvk.News;
import com.forsazhgames.newsvk.R;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by Forsazhrus on 20.09.2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.NewsViewHolder> {

    public static class NewsViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        ImageView photo;
        TextView date, text;

        NewsViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            photo = (ImageView) itemView.findViewById(R.id.imageView);
            date = (TextView) itemView.findViewById(R.id.dateView);
            text = (TextView) itemView.findViewById(R.id.textView);
        }
    }

    List<News> news;
    Context context;

    public RVAdapter(Context context, List<News> news) {
        this.context = context;
        this.news = news;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_news, viewGroup, false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder newsViewHolder, int i) {
        Picasso.with(context).load(news.get(i).getPhotoURL()).into(newsViewHolder.photo);
        newsViewHolder.date.setText(news.get(i).getDate());
        newsViewHolder.text.setText(news.get(i).getText());
    }

    @Override
    public int getItemCount() {
        return news.size();
    }
}