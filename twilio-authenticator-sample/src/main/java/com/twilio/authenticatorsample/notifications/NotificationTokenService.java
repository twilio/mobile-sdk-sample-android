package com.twilio.authenticatorsample.notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.twilio.authenticator.TwilioAuthenticator;
import com.twilio.authenticator.TwilioAuthenticatorTaskCallback;
import com.twilio.authenticatorsample.SampleApp;

/**
 * Created by jsuarez on 11/18/16.
 */

public class NotificationTokenService extends FirebaseInstanceIdService implements TwilioAuthenticatorTaskCallback<Void> {

    private static final String TAG = NotificationTokenService.class.getSimpleName();
    private TwilioAuthenticator twilioAuthenticator;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        twilioAuthenticator = ((SampleApp) getApplicationContext()).getTwilioAuthenticator();

        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        if (twilioAuthenticator.isDeviceRegistered()) {
            sendPushToken(refreshedToken);
        }
    }

    private void sendPushToken(final String refreshedToken) {
        twilioAuthenticator.setPushToken(refreshedToken, this);
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
