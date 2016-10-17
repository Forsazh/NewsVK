package com.forsazhgames.simplevk.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.forsazhgames.simplevk.R;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String[] scope = new String[]{VKScope.MESSAGES, VKScope.FRIENDS, VKScope.WALL};
    private Button loginButton, showNewsButton, showBirthdaysButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeButtons();
        if (VKSdk.wakeUpSession(getApplicationContext())) {
            changeVisibility();
        }
    }

    private void initializeButtons() {
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);

        showNewsButton = (Button) findViewById(R.id.showNews_button);
        showNewsButton.setOnClickListener(this);

        showBirthdaysButton = (Button) findViewById(R.id.showBirthdays_button);
        showBirthdaysButton.setOnClickListener(this);

        logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(this);
    }

    private void changeVisibility() {
        if (loginButton.getVisibility() == View.VISIBLE) {
            loginButton.setVisibility(View.GONE);
            showNewsButton.setVisibility(View.VISIBLE);
            showBirthdaysButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            loginButton.setVisibility(View.VISIBLE);
            showNewsButton.setVisibility(View.GONE);
            showBirthdaysButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                changeVisibility();
            }

            @Override
            public void onError(VKError error) {
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        switch (buttonId) {
            case R.id.login_button:
                VKSdk.login(MainActivity.this, scope);
                break;
            case R.id.showNews_button:
                this.startActivity(new Intent(this, NewsActivity.class));
                break;
            case R.id.showBirthdays_button:
                this.startActivity(new Intent(this, BirthdayActivity.class));
                break;
            case R.id.logout_button:
                VKSdk.logout();
                changeVisibility();
                break;
        }
    }
}