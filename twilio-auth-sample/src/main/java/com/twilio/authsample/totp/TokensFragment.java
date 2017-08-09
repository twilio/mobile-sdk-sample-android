package com.twilio.authsample.totp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twilio.auth.TwilioAuth;
import com.twilio.auth.external.TOTPCallback;
import com.twilio.authsample.App;
import com.twilio.authsample.R;
import com.twilio.authsample.ui.views.AuthyTimerView;

/**
 * A simple {@link Fragment} to display a TOTP code with a timer
 */
public class TokensFragment extends Fragment implements TOTPCallback, TokenTimer.OnTimerListener {

    private final static int TOTP_UPDATE_INTERVAL_MILLIS = 20000;
    private static final int TICK_INTERVAL_TIME_MILLIS = 50;

    private TwilioAuth twilioAuth;
    private Handler handler = new Handler();
    private TokenTimer tokenTimer;
    // Runnable
    Runnable updateTOTPRunnable = new Runnable() {
        public void run() {
            updateTOTP();
            handler.postDelayed(this, TOTP_UPDATE_INTERVAL_MILLIS);
        }
    };
    // Views
    private TextView totpView;
    private AuthyTimerView authyTimerView;

    public TokensFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of Token Fragment
     *
     * @return A new instance of fragment TokensFragment.
     */
    public static TokensFragment newInstance() {
        return new TokensFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVars();
        initListeners();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tokens, container, false);
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        startTOTPGeneration();
    }

    @Override
    public void onStop() {
        stopTOTPGeneration();
        super.onStop();
    }

    @Override
    public void onTOTPReceived(String totp) {
        totpView.setText(totp);
    }

    @Override
    public void onTOTPError(Exception e) {

    }

    private void initViews(View rootView) {
        totpView = (TextView) rootView.findViewById(R.id.totp);
        authyTimerView = (AuthyTimerView) rootView.findViewById(R.id.timer);
        authyTimerView.setArcColor(getResources().getColor(R.color.colorAccent));
        authyTimerView.setArcBackgroundColor(getResources().getColor(R.color.lightGrey));
        authyTimerView.setDotColor(getResources().getColor(android.R.color.transparent));
        authyTimerView.setTimerBackgroundColor(getResources().getColor(R.color.background_color));
    }


    private void initVars() {
        twilioAuth = ((App) getContext().getApplicationContext()).getTwilioAuth();
        tokenTimer = new TokenTimer(TICK_INTERVAL_TIME_MILLIS, TOTP_UPDATE_INTERVAL_MILLIS);
    }


    private void initListeners() {
        tokenTimer.setOnTimerListener(this);
    }

    private void startTOTPGeneration() {
        updateTOTP();
        tokenTimer.start();
        handler.postDelayed(updateTOTPRunnable,
                TOTP_UPDATE_INTERVAL_MILLIS);
    }

    private void stopTOTPGeneration() {
        tokenTimer.stop();
        handler.removeCallbacks(updateTOTPRunnable);
    }

    private void updateTOTP() {
        twilioAuth.getTOTP(this);
        tokenTimer.restart();
    }

    @Override
    public void onTokenTimerElapsed(TokenTimer tokenTimer) {
        tokenTimer.restart();
    }

    @Override
    public void onTimerTick(TokenTimer tokenTimer) {
        authyTimerView.setCurrentTime((int) tokenTimer.getRemainingMillis());
        authyTimerView.setTotalTime(TOTP_UPDATE_INTERVAL_MILLIS);
    }
}
