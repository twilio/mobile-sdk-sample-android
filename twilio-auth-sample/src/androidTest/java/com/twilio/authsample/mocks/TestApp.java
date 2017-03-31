package com.twilio.authsample.mocks;

import com.twilio.auth.TwilioAuth;
import com.twilio.authsample.App;

/**
 * Created by jsuarez on 5/31/16.
 */
public class TestApp extends App {

    private MockTwilioAuth mockAuthy;
    public static TestApp TEST_APP;

    @Override
    public void onCreate() {
        super.onCreate();
        TEST_APP = this;
        mockAuthy = new MockTwilioAuth(this, false);
    }

    @Override
    public TwilioAuth getTwilioAuth() {
        return mockAuthy;
    }

    public void setMockAuthy(MockTwilioAuth mockAuthy) {
        this.mockAuthy = mockAuthy;
    }

}
