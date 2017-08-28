package com.twilio.authsample.mocks;

import android.provider.Settings;

import com.twilio.auth.TwilioAuth;
import com.twilio.authsample.App;

/**
 * Created by jsuarez on 8/14/17.
 */

public class TestApp extends App {

    MockTwilioAuth mockTwilioAuth;

    @Override
    public void onCreate() {
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ANDROID_ID, "test_android_id");
        super.onCreate();
    }

    @Override
    public TwilioAuth getTwilioAuth() {
        return mockTwilioAuth;
    }

    public void setTwilioAuth(MockTwilioAuth mockTwilioAuth) {
        this.mockTwilioAuth = mockTwilioAuth;
    }
}
