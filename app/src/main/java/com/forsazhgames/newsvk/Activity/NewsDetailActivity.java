package com.forsazhgames.newsvk.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.forsazhgames.newsvk.Adapter.AttachmentRVAdapter;
import com.forsazhgames.newsvk.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Forsazhrus on 23.09.2016.
 */
public class NewsDetailActivity extends Activity {

    private RecyclerView recyclerView;
    private ImageView imageView;
    private TextView dateView, textView, likeView;
    private ArrayList<String> attachment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail);
        initViews();
        setData();
    }

    private void initViews() {
        imageView = (ImageView) findViewById(R.id.imageViewDetail);
        dateView = (TextView) findViewById(R.id.dateViewDetail);
        textView = (TextView) findViewById(R.id.textViewDetail);
        likeView = (TextView) findViewById(R.id.likeViewDetail);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewDetail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setData() {
        Intent intent = getIntent();
        attachment = intent.getStringArrayListExtra("attachment");
        Picasso.with(this).load(intent.getStringExtra("image")).into(imageView);
        dateView.setText(intent.getStringExtra("date"));
        textView.setText(intent.getStringExtra("text"));
        likeView.setText(getString(R.string.total_likes) + intent.getLongExtra("like", 0));
        recyclerView.setAdapter(new AttachmentRVAdapter(this, attachment));
    }
}
