package com.twilio.authsample.totp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
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
 * A simple {@link Fragment} subclass.
 * Use the {@link TokensFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TokensFragment extends Fragment implements TOTPCallback, TokenTimer.OnTimerListener {

    private final static int TOTP_UPDATE_INTERVAL_MILLIS = 20000;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TwilioAuth twilioAuth;
    private Handler handler = new Handler();
    private TokenTimer tokenTimer;

    // Views
    private TextView totpView;
    private AuthyTimerView authyTimerView;


    // Runnable
    Runnable updateTOTPRunnable = new Runnable() {
        public void run() {
            updateTOTP();
            handler.postDelayed(this, TOTP_UPDATE_INTERVAL_MILLIS);
        }
    };

    public TokensFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TokensFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TokensFragment newInstance(String param1, String param2) {
        TokensFragment fragment = new TokensFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

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
        authyTimerView.setArcColor(getResources().getColor(R.color.darkGrey));
        authyTimerView.setDotColor(getResources().getColor(R.color.colorAccent));
    }


    private void initVars() {
        twilioAuth = ((App) getContext().getApplicationContext()).getTwilioAuth();
        tokenTimer = new TokenTimer(1000, TOTP_UPDATE_INTERVAL_MILLIS);
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
