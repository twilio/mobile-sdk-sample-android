package com.twilio.authsample.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.twilio.authenticator.TwilioAuthenticator;
import com.twilio.authenticator.external.AuthenticatorObserver;
import com.twilio.authenticator.external.AuthenticatorToken;
import com.twilio.authenticator.external.TOTP;
import com.twilio.authenticator.external.TOTPs;
import com.twilio.authsample.App;
import com.twilio.authsample.R;
import com.twilio.authsample.totp.TokenTimer;
import com.twilio.authsample.ui.views.AuthyTimerView;
import com.twilio.authsample.utils.MessageHelper;

public class TokenDetailsActivity extends AppCompatActivity implements TokenTimer.OnTimerListener, AuthenticatorObserver {

    static final String EXTRA_APP_ID = "APP_ID";
    private static final int TICK_INTERVAL_TIME_MILLIS = 50;

    private TwilioAuthenticator twilioAuthenticator;
    private TokenTimer tokenTimer;
    private String appId;
    private String title;
    private TOTP currentTOTP;
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
        setContentView(R.layout.activity_token_details);

        twilioAuthenticator = ((App) getApplicationContext()).getTwilioAuthenticator();

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
        if (currentTOTP != null) {
            authyTimerView.setTotalTime((int) currentTOTP.getUpdateIntervalMillis());
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
    public void onTOTPsUpdated(@NonNull TOTPs totps) {
        AuthenticatorToken authenticatorToken = totps.getTOTP(appId);
        currentTOTP = authenticatorToken.getTotp();
        updateView(currentTOTP);
    }

    @Override
    public void onAppAdded(@NonNull AuthenticatorToken app) {

    }

    @Override
    public void onAppDeleted(@NonNull String appId) {
        if (this.appId.equals(appId)) {
            Snackbar snackbar = messageHelper.show(authyTimerView, "App was deleted");
            snackbar.addCallback(appDeletedCallback);
            finish();
        }
    }

    @Override
    public void onAppUpdated(@NonNull AuthenticatorToken app) {

    }

    private void updateView(TOTP totp) {
        totpView.setText(totp.getToken());
        getTokenTimer(totp.getUpdateIntervalMillis()).restart();
    }

    TokenTimer getTokenTimer(long totpUpdateIntervalMillis) {
        if (tokenTimer == null) {
            tokenTimer = new TokenTimer(TICK_INTERVAL_TIME_MILLIS, totpUpdateIntervalMillis);
            tokenTimer.setOnTimerListener(this);
        }
        return tokenTimer;
    }

}
