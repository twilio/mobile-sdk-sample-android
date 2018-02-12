package com.twilio.authenticatorsample.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.twilio.authenticator.TwilioAuthenticator;
import com.twilio.authenticator.external.App;
import com.twilio.authenticator.external.AuthenticatorObserver;
import com.twilio.authenticatorsample.R;
import com.twilio.authenticatorsample.SampleApp;
import com.twilio.authenticatorsample.totp.TokenTimer;
import com.twilio.authenticatorsample.ui.views.AuthyTimerView;
import com.twilio.authenticatorsample.utils.MessageHelper;

import java.util.List;

public class AppDetailActivity extends AppCompatActivity implements TokenTimer.OnTimerListener, AuthenticatorObserver {

    static final String EXTRA_APP_ID = "APP_ID";
    private static final int TICK_INTERVAL_TIME_MILLIS = 50;

    private TwilioAuthenticator twilioAuthenticator;
    private TokenTimer tokenTimer;
    private String appId;
    private String title;
    private com.twilio.authenticator.external.App currentApp;
    private Snackbar.Callback appDeletedCallback = new Snackbar.Callback() {
        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            super.onDismissed(transientBottomBar, event);
            finish();
        }
    };

    // Views
    private TextView totpView;
    private AuthyTimerView authyTimerView;
    private MessageHelper messageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        twilioAuthenticator = ((SampleApp) getApplicationContext()).getTwilioAuthenticator();

        initVars();
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        totpView = (TextView) findViewById(R.id.totp);
        authyTimerView = (AuthyTimerView) findViewById(R.id.timer);
        authyTimerView.setArcColor(getResources().getColor(R.color.colorAccent));
        authyTimerView.setArcBackgroundColor(getResources().getColor(R.color.lightGrey));
        authyTimerView.setDotColor(getResources().getColor(android.R.color.transparent));
        authyTimerView.setTimerBackgroundColor(getResources().getColor(R.color.background_color));

        // Enable the Up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initVars() {
        messageHelper = new MessageHelper();
        appId = getIntent().getStringExtra(EXTRA_APP_ID);
        title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
    }

    @Override
    public void onTokenTimerElapsed(TokenTimer tokenTimer) {
        tokenTimer.restart();
    }

    @Override
    public void onTimerTick(TokenTimer tokenTimer) {
        authyTimerView.setCurrentTime((int) tokenTimer.getRemainingMillis());
        if (currentApp != null) {
            authyTimerView.setTotalTime(currentApp.getCodeDurationInSeconds() * 1000);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        twilioAuthenticator.addObserver(this);
    }

    @Override
    public void onStop() {
        messageHelper.dismiss();
        twilioAuthenticator.removeObserver(this);
        messageHelper.removeCallback(appDeletedCallback);
        super.onStop();
    }

    @Override
    public void onAppDeleted(@NonNull String appId) {
        if (this.appId.equals(appId)) {
            Snackbar snackbar = messageHelper.show(authyTimerView, "App was deleted");
            snackbar.addCallback(appDeletedCallback);
            finish();
        }
    }

    private void updateView(com.twilio.authenticator.external.App app) {
        totpView.setText(app.getCurrentCode());
        getTokenTimer(app.getCodeDurationInSeconds() * 1000).restart();
    }

    TokenTimer getTokenTimer(long codeDurationMillis) {
        if (tokenTimer == null) {
            tokenTimer = new TokenTimer(TICK_INTERVAL_TIME_MILLIS, codeDurationMillis);
            tokenTimer.setOnTimerListener(this);
        }
        return tokenTimer;
    }

    @Override
    public void onNewCode(@NonNull List<App> apps) {
        for (App app : apps) {
            if (app.getId().equals(appId)) {
                currentApp = app;
                break;
            }
        }

        updateView(currentApp);
    }

    @Override
    public void onAppAdded(@NonNull App app) {
        // Not needed
    }

    @Override
    public void onAppUpdated(@NonNull App app) {
        // Not needed
    }
}
