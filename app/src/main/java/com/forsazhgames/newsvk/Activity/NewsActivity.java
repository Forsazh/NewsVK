package com.forsazhgames.newsvk.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.forsazhgames.newsvk.News;
import com.forsazhgames.newsvk.R;
import com.forsazhgames.newsvk.Adapter.RVAdapter;
import com.forsazhgames.newsvk.RecyclerItemClickListener;
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
    final int COUNT = 100;
    private boolean isInit, startedDownload;
    private VKRequest request;
    private String startValue;
    private Button back, refresh;
    private List<News> news;
    private RecyclerView rv;
    private RVAdapter adapter;
    private LinearLayoutManager llm;
    private int totalItemCount, lastVisibleItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);

        llm = new LinearLayoutManager(this);
        initRV();
        initializeButtons();
        initializeNews();
        updateAdapter();
    }

    private void initRV() {
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(llm);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = llm.getItemCount();//сколько всего элементов
                lastVisibleItem = llm.findLastVisibleItemPosition();//какая позиция последнего элемента на экране
                if (!startedDownload && lastVisibleItem == totalItemCount - 1) {
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

    private void initializeButtons() {
        back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(this);

        refresh = (Button) findViewById(R.id.refresh_button);
        refresh.setOnClickListener(this);
    }


    private void initializeNews() {
        isInit = true;
        startValue = "";
        news = new ArrayList<>();
        request = new VKRequest("newsfeed.get",
                VKParameters.from(VKApiConst.FILTERS, "post", VKApiConst.COUNT, COUNT));
        downloadNews();
    }

    private void updateAdapter() {
        adapter = new RVAdapter(NewsActivity.this, news);
        rv.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        switch (buttonId) {
            case R.id.back_button:
                this.startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.refresh_button:
                initializeNews();
                break;
        }
    }

    private void downloadMoreNews() {
        isInit = false;
        request = new VKRequest("newsfeed.get",
                VKParameters.from(VKApiConst.FILTERS, "post",
                        VKApiConst.COUNT, COUNT, "start_from", startValue));
        downloadNews();
    }

    private void downloadNews() {
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
                if (isInit) {
                    updateAdapter();
                } else {
                    rv.getAdapter().notifyItemRangeInserted(prevSize, news.size());
                }
            }
        });
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