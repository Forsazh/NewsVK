package com.forsazhgames.simplevk.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

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
    private LinearLayoutManager llm;
    private Button back, refresh;
    private List<User> users, usersTemp = new ArrayList<>();
    private RecyclerView rv;
    private BirthdayRVAdapter adapter;
    private VKRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birthday);

        llm = new LinearLayoutManager(this);
        initRV();
        initializeButtons();
        initializeBirthdays();
        updateAdapter();
    }

    private void initRV() {
        rv = (RecyclerView) findViewById(R.id.rvBirthday);
        rv.setLayoutManager(llm);
    }

    private void initializeButtons() {
        back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(this);

        refresh = (Button) findViewById(R.id.refresh_button);
        refresh.setOnClickListener(this);
    }

    private void initializeBirthdays() {
        users = new LinkedList<>();
        if (request != null) {
            request.cancel();
        }
        request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS,
                "id, first_name, last_name, bdate, photo_100, lists"));
        downloadNews();
    }

    private void updateAdapter() {
        adapter = new BirthdayRVAdapter(this, users);
        rv.setAdapter(adapter);
    }

    private void downloadNews() {
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
            }
        });
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
            case R.id.back_button:
                this.startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.refresh_button:
                initializeBirthdays();
                break;
        }
    }
}
