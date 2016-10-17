package com.forsazhgames.simplevk.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.forsazhgames.simplevk.Activity.BirthdayActivity;
import com.forsazhgames.simplevk.Models.User;
import com.forsazhgames.simplevk.R;
import com.squareup.picasso.Picasso;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

/**
 * Created by Forsazhrus on 12.10.2016.
 */
public class BirthdayRVAdapter extends RecyclerView.Adapter<BirthdayRVAdapter.BirthdayViewHolder> {

    public static class BirthdayViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        ImageView photo;
        TextView bDate, name;
        DateTimeFormatter formatter = DateTimeFormat.forPattern(BirthdayActivity.formats[0]);

        BirthdayViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cvBirthday);
            photo = (ImageView) itemView.findViewById(R.id.imageViewBirthday);
            bDate = (TextView) itemView.findViewById(R.id.bDateViewBirthday);
            name = (TextView) itemView.findViewById(R.id.nameViewBirthday);
        }
    }

    List<User> users;
    Context context;

    public BirthdayRVAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public BirthdayViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_birthday, viewGroup, false);
        return new BirthdayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BirthdayViewHolder newsViewHolder, int i) {
        Picasso.with(context).load(users.get(i).getPhotoURL()).into(newsViewHolder.photo);
        newsViewHolder.bDate.setText(users.get(i).getBDate()
                .toString(DateTimeFormat.forPattern(users.get(i).getDateFormat())));
        newsViewHolder.name.setText(users.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
