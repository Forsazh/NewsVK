package com.forsazhgames.newsvk.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.forsazhgames.newsvk.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Forsazhrus on 26.09.2016.
 */
public class AttachmentRVAdapter extends RecyclerView.Adapter<AttachmentRVAdapter.AttachmentViewHolder> {

    public static class AttachmentViewHolder extends RecyclerView.ViewHolder {

        ImageView attachment;

        public AttachmentViewHolder(View view) {
            super(view);
            attachment = (ImageView) view.findViewById(R.id.attachmentImageView);
        }
    }

    Context context;
    ArrayList<String> imageUrls;

    public AttachmentRVAdapter(Context context, ArrayList<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public AttachmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_in_detail, parent, false);
        return new AttachmentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AttachmentViewHolder holder, int i) {
        Picasso.with(context).load(imageUrls.get(i)).into(holder.attachment);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }
}