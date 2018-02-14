package com.twilio.authenticatorsample.appdetail;

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
import com.twilio.authenticatorsample.ui.views.AuthyTimerView;
import com.twilio.authenticatorsample.utils.MessageHelper;

import java.util.List;

public class AppDetailFragment extends Fragment implements TokenTimer.OnTimerListener, AuthenticatorObserver {

    private static final int TICK_INTERVAL_TIME_MILLIS = 50;

    private TwilioAuthenticator twilioAuthenticator;
    private TokenTimer tokenTimer;
    private Long appId;
    private com.twilio.authenticator.external.App currentApp;
    private Snackbar.Callback appDeletedCallback = new Snackbar.Callback() {
        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            super.onDismissed(transientBottomBar, event);
            getActivity().onBackPressed();
        }
    };

    // Views
    private TextView totpView;
    private AuthyTimerView authyTimerView;
    private MessageHelper messageHelper;

    /**
     * Use this factory method to create a new instance of Requests fragment
     *
     * @return A new instance of fragment RequestsFragment.
     */
    public static AppDetailFragment newInstance(Long appId, TwilioAuthenticator twilioAuthenticator) {

        AppDetailFragment appDetailFragment = new AppDetailFragment();
        appDetailFragment.twilioAuthenticator = twilioAuthenticator;
        appDetailFragment.appId = appId;
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

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initVars() {
        messageHelper = new MessageHelper();
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
    public void onResume() {
        super.onResume();
        twilioAuthenticator.addObserver(this);
    }

    @Override
    public void onPause() {
        twilioAuthenticator.removeObserver(this);
        super.onPause();
    }

    @Override
    public void onStop() {
        messageHelper.dismiss();
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

        if (currentApp != null) {
            updateView(currentApp);
        }
    }

    @Override
    public void onAppAdded(List<App> apps) {

    }

    @Override
    public void onAppDeleted(final List<Long> appIds) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appIds.contains(appId)) {
                    Snackbar snackbar = messageHelper.show(authyTimerView, "App was deleted");
                    snackbar.addCallback(appDeletedCallback);
                }
            }
        });
    }

    @Override
    public void onError(Exception exception) {
        Snackbar snackbar = messageHelper.show(authyTimerView, exception.getMessage());

    }

    @Override
    public void onAppUpdated(List<App> apps) {
        // Useful if we are displaying the name of the app
    }
}
