package com.twilio.authsample.mocks;

import com.twilio.authenticator.TwilioAuthenticator;
import com.twilio.authsample.App;

/**
 * Created by jsuarez on 5/31/16.
 */
public class TestApp extends App {

    public static TestApp TEST_APP;
    private MockTwilioAuthenticator mockTwilioAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        TEST_APP = this;
        mockTwilioAuthenticator = new MockTwilioAuthenticator(this, false);
    }

    @Override
    public TwilioAuthenticator getTwilioAuthenticator() {
        return mockTwilioAuthenticator;
    }

    public void setMockTwilioAuthenticator(MockTwilioAuthenticator mockTwilioAuthenticator) {
        this.mockTwilioAuthenticator = mockTwilioAuthenticator;
    }

}
