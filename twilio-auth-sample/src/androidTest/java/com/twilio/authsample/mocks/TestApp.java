package com.twilio.authsample.mocks;

import com.twilio.auth.TwilioAuth;
import com.twilio.authsample.App;

/**
 * Created by jsuarez on 5/31/16.
 */
public class TestApp extends App {

    public static TestApp TEST_APP;
    private MockTwilioAuth mockTwilioAuth;

    @Override
    public void onCreate() {
        super.onCreate();
        TEST_APP = this;
        mockTwilioAuth = new MockTwilioAuth(this, false);
    }

    @Override
    public TwilioAuth getTwilioAuth() {
        return mockTwilioAuth;
    }

    public void setMockTwilioAuth(MockTwilioAuth mockTwilioAuth) {
        this.mockTwilioAuth = mockTwilioAuth;
    }

}
