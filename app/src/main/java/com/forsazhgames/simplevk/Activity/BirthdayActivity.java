package com.forsazhgames.simplevk.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.forsazhgames.simplevk.Adapter.BirthdayRVAdapter;
import com.forsazhgames.simplevk.Models.User;
import com.forsazhgames.simplevk.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Forsazhrus on 12.10.2016.
 */
public class BirthdayActivity extends Activity implements View.OnClickListener {

    public static final String[] formats = new String[]{"dd.MM.yyyy", "dd.MM"};
    public static final int DOWNLOADING = 0, DOWNLOADED = 1;
    private LinearLayoutManager llm;
    private Button refresh;
    private ProgressBar progressBar;
    private Handler handler;
    private List<User> users, usersTemp = new ArrayList<>();
    private RecyclerView rv;
    private BirthdayRVAdapter adapter;
    private VKRequest request;
    private Thread thread;
    private boolean inProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birthday);

        llm = new LinearLayoutManager(this);
        initHandler();
        initRV();
        initializeViews();
        initializeBirthdays();
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
        rv = (RecyclerView) findViewById(R.id.rvBirthday);
        rv.setLayoutManager(llm);
    }

    private void initializeViews() {
        refresh = (Button) findViewById(R.id.refresh_button);
        refresh.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void initializeBirthdays() {
        handler.sendEmptyMessage(DOWNLOADING);
        users = new LinkedList<>();
        if (request != null) {
            request.cancel();
        }
        request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS,
                "id, first_name, last_name, bdate, photo_100, lists"));
        downloadBirthdays();
    }

    private void updateAdapter() {
        adapter = new BirthdayRVAdapter(this, users);
        rv.setAdapter(adapter);
    }

    private void downloadBirthdays() {
        if (!inProcess) {
            thread = new Thread(new Runnable() {
                public void run() {
                    inProcess = true;
                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            VKUsersArray usersArray = (VKUsersArray) response.parsedModel;
                            users.clear();
                            for (VKApiUserFull userFull : usersArray) {
                                DateTime birthDate = null;
                                String format = null;
                                if (!TextUtils.isEmpty(userFull.bdate)) {
                                    for (int i = 0; i < formats.length; i++) {
                                        format = formats[i];
                                        try {
                                            birthDate = DateTimeFormat.forPattern(format).parseDateTime(userFull.bdate);
                                        } catch (Exception ignored) {
                                        }
                                        if (birthDate != null) {
                                            break;
                                        }
                                    }
                                    users.add(new User(userFull.toString(), birthDate, format, userFull.photo_100));
                                }
                            }
                            Collections.sort(users);
                            sortUsersByBDate();
                            updateAdapter();
                            handler.sendEmptyMessage(DOWNLOADED);
                            inProcess = false;
                        }
                    });
                }
            });
            thread.start();
        }
    }

    private void sortUsersByBDate() {
        DateTime today = DateTime.now();
        today = today.minusYears(today.getYear()).minusMillis(today.getMillisOfDay());
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getBDate().minusYears(users.get(i).getBDate().getYear())
                    .isBefore(today)) {
                usersTemp.add(users.get(i));
                users.remove(i);
                i--;
            }
        }
        users.addAll(usersTemp);
    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        switch (buttonId) {
            case R.id.refresh_button:
                initializeBirthdays();
                break;
        }
    }
}
