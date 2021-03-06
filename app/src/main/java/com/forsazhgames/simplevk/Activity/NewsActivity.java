package com.forsazhgames.simplevk.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.forsazhgames.simplevk.Models.News;
import com.forsazhgames.simplevk.R;
import com.forsazhgames.simplevk.Adapter.NewsRVAdapter;
import com.forsazhgames.simplevk.RecyclerItemClickListener;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Forsazhrus on 19.09.2016.
 */
public class NewsActivity extends Activity implements View.OnClickListener {

    final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    public static final int DOWNLOADING = 0, DOWNLOADED = 1;
    final int COUNT = 100;
    private boolean fromStart, startedDownload;
    private VKRequest request;
    private String startValue;
    private Button refresh;
    private ProgressBar progressBar;
    private Handler handler;
    private List<News> news;
    private RecyclerView rv;
    private NewsRVAdapter adapter;
    private LinearLayoutManager llm;
    private int totalItemCount, lastVisibleItem;
    private Thread thread;
    private boolean inProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);

        llm = new LinearLayoutManager(this);
        initHandler();
        initRV();
        initializeViews();
        initializeNews();
        updateAdapter();
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DOWNLOADING:
                        refresh.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case DOWNLOADED:
                        refresh.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        break;
                }
            }
        };
    }

    private void initRV() {
        rv = (RecyclerView) findViewById(R.id.rvNews);
        rv.setLayoutManager(llm);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = llm.getItemCount();//сколько всего элементов
                lastVisibleItem = llm.findLastVisibleItemPosition();//какая позиция последнего элемента на экране
                if (!startedDownload && lastVisibleItem == totalItemCount - 1 && !inProcess) {
                    startedDownload = true;
                    downloadMoreNews();
                } else {
                    startedDownload = false;
                }
            }
        });
        rv.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                News n = news.get(position);
                Intent intent = new Intent(NewsActivity.this, NewsDetailActivity.class);
                intent.putExtra("image", n.getPhotoURL());
                intent.putExtra("date", n.getDate());
                intent.putExtra("text", n.getText());
                intent.putExtra("like", n.getLike());
                intent.putStringArrayListExtra("attachment", n.getAttachmentURLs());
                startActivity(intent);
            }
        }));
    }

    private void initializeViews() {
        refresh = (Button) findViewById(R.id.refresh_button);
        refresh.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void initializeNews() {
        handler.sendEmptyMessage(DOWNLOADING);
        fromStart = true;
        startValue = "";
        news = new ArrayList<>();
        if (request != null) {
            request.cancel();
        }
        request = new VKRequest("newsfeed.get",
                VKParameters.from(VKApiConst.FILTERS, "post", VKApiConst.COUNT, COUNT));
        downloadNews();
    }

    private void updateAdapter() {
        adapter = new NewsRVAdapter(NewsActivity.this, news);
        rv.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        switch (buttonId) {
            case R.id.refresh_button:
                initializeNews();
                break;
        }
    }

    private void downloadMoreNews() {
        handler.sendEmptyMessage(DOWNLOADING);
        fromStart = false;
        request = new VKRequest("newsfeed.get",
                VKParameters.from(VKApiConst.FILTERS, "post",
                        VKApiConst.COUNT, COUNT, "start_from", startValue));
        downloadNews();
    }

    private void downloadNews() {
        if (!inProcess) {
            thread = new Thread(new Runnable() {
                public void run() {
                    inProcess = true;
                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            Map<Long, String> profileID, groupID;
                            profileID = new HashMap<>();
                            groupID = new HashMap<>();
                            int prevSize = news.size();
                            try {
                                JSONObject jsonResponse = response.json.getJSONObject("response");
                                if (!startValue.equals(jsonResponse.getString("next_from"))) {
                                    putInMap(jsonResponse.getJSONArray("profiles"), profileID);
                                    putInMap(jsonResponse.getJSONArray("groups"), groupID);
                                    startValue = jsonResponse.getString("next_from");
                                    prepareNews(jsonResponse.getJSONArray("items"), profileID, groupID);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (fromStart) {
                                updateAdapter();
                            } else {
                                rv.getAdapter().notifyItemRangeInserted(prevSize, news.size());
                            }
                            handler.sendEmptyMessage(DOWNLOADED);
                            inProcess = false;
                        }
                    });
                }
            });
            thread.start();
        }
    }

    private void putInMap(JSONArray array, Map map) throws JSONException {
        JSONObject object;
        for (int i = 0; i < array.length(); i++) {
            object = array.getJSONObject(i);
            map.put(object.getLong("id"), object.getString("photo_100"));
        }
    }

    private void prepareNews(JSONArray array, Map<Long, String> pID, Map<Long, String> gID) throws JSONException {
        Date date;
        JSONObject object, attObject;
        JSONArray attachment;
        Long sourceID, like;
        String photoURL, text, dateString, attachmentURL, type;
        ArrayList<String> attachmentURLs;
        for (int i = 0; i < array.length(); i++) {
            object = array.getJSONObject(i);
            sourceID = object.getLong("source_id");
            photoURL = sourceID > 0 ? pID.get(sourceID) : gID.get(-sourceID);
            text = object.get("text").toString();
            date = new Date(object.getLong("date") * 1000);
            dateString = DATE_FORMAT.format(date);
            like = object.getJSONObject("likes").getLong("count");
            attachment = object.optJSONArray("attachments");
            attachmentURLs = new ArrayList<>();
            if (attachment != null) {
                for (int j = 0; j < attachment.length(); j++) {
                    attObject = attachment.getJSONObject(j);
                    type = attObject.getString("type");
                    if (type.equals("link")) {
                        attObject = attObject.getJSONObject("link");
                    }
                    if (type.equals("photo") || type.equals("link")) {
                        attachmentURL = attObject.getJSONObject("photo").optString("photo_604");
                        if (attachmentURL != null) {
                            attachmentURLs.add(attachmentURL);
                        } else {
                            attachmentURLs.add(attObject.getJSONObject("photo").optString("photo_130"));
                        }
                    }
                }
            }
            if (!text.isEmpty()) {
                news.add(new News(photoURL, dateString, text, like, attachmentURLs));
            }
        }
    }
}