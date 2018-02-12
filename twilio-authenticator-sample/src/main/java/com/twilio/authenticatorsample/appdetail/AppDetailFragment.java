package com.twilio.authenticatorsample.main;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class AppDetailFragment extends Fragment implements TokenTimer.OnTimerListener, AuthenticatorObserver {

    private static final int TICK_INTERVAL_TIME_MILLIS = 50;

    private TwilioAuthenticator twilioAuthenticator;
    private TokenTimer tokenTimer;
    private Long appId;
    private String title;
    private com.twilio.authenticator.external.App currentApp;
    private Snackbar.Callback appDeletedCallback = new Snackbar.Callback() {
        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            super.onDismissed(transientBottomBar, event);
//            finish();
        }
    };

    // Views
    private TextView totpView;
    private AuthyTimerView authyTimerView;
    private MessageHelper messageHelper;

    public AppDetailFragment() {

    }

    /**
     * Use this factory method to create a new instance of Requests fragment
     *
     * @return A new instance of fragment RequestsFragment.
     */
    public static AppDetailFragment newInstance(Long appId) {
        Bundle args = new Bundle();
        args.putLong("appId", appId);
        AppDetailFragment appDetailFragment = new AppDetailFragment();
        appDetailFragment.setArguments(args);
        return appDetailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_app_detail, container, false);
        initViews(rootView);
        initVars();
        return rootView;
    }

    private void initViews(View rootView) {

        totpView = (TextView) rootView.findViewById(R.id.totp);
        authyTimerView = (AuthyTimerView) rootView.findViewById(R.id.timer);
        authyTimerView.setArcColor(getResources().getColor(R.color.colorAccent));
        authyTimerView.setArcBackgroundColor(getResources().getColor(R.color.lightGrey));
        authyTimerView.setDotColor(getResources().getColor(android.R.color.transparent));
        authyTimerView.setTimerBackgroundColor(getResources().getColor(R.color.background_color));

        // Enable the Up button
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
//                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initVars() {
        messageHelper = new MessageHelper();
        SampleApp sampleApp = (SampleApp) getActivity().getApplicationContext();
        twilioAuthenticator = (sampleApp).getTwilioAuthenticator();
        appId = getArguments().getLong("appId");
//        title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
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
            if (app.getId() == appId) {
                currentApp = app;
                break;
            }
        }

        updateView(currentApp);
    }

    @Override
    public void onAppAdded(List<App> apps) {

    }

    @Override
    public void onAppDeleted(List<Long> appIds) {
        if (this.appId.equals(appId)) {
            Snackbar snackbar = messageHelper.show(authyTimerView, "App was deleted");
            snackbar.addCallback(appDeletedCallback);
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onError(Exception exception) {

    }

    @Override
    public void onAppUpdated(List<App> apps) {

    }
}
