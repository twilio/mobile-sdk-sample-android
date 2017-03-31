package com.twilio.authsample.notifications;

import android.util.Log;

import com.twilio.auth.TwilioAuth;
import com.twilio.authsample.App;
import com.twilio.authsample.utils.AuthyActivityListener;
import com.twilio.authsample.utils.AuthyTask;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by jsuarez on 11/18/16.
 */

public class NotificationTokenService extends FirebaseInstanceIdService implements AuthyActivityListener<Void> {

    private static final String TAG = NotificationTokenService.class.getSimpleName();
    private TwilioAuth twilioAuth;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        twilioAuth = ((App) getApplicationContext()).getTwilioAuth();

        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        if (twilioAuth.isDeviceRegistered()) {
            sendPushToken(refreshedToken);
        }
    }

    private void sendPushToken(final String refreshedToken) {
        new AuthyTask<Void>(this) {
            @Override
            public Void executeOnBackground() {
                twilioAuth.setPushToken(refreshedToken);
                return null;
            }
        }.execute();
    }

    @Override
    public void onSuccess(Void result) {
        Log.d(TAG, "Push token uploaded");
    }

    @Override
    public void onError(Exception exception) {
        Log.d(TAG, "Unable to upload push token", exception);
    }
}
