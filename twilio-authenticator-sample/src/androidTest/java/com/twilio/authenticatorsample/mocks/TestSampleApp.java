package com.twilio.authenticatorsample.mocks;

import com.twilio.authenticator.TwilioAuthenticator;
import com.twilio.authenticatorsample.SampleApp;

/**
 * Created by jsuarez on 5/31/16.
 */
public class TestSampleApp extends SampleApp {

    public static TestSampleApp TEST_APP;
    private MockTwilioAuthenticator mockTwilioAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        TEST_APP = this;
        mockTwilioAuthenticator = new MockTwilioAuthenticator(false);
    }

    @Override
    public TwilioAuthenticator getTwilioAuthenticator() {
        return mockTwilioAuthenticator;
    }

    public void setMockTwilioAuthenticator(MockTwilioAuthenticator mockTwilioAuthenticator) {
        this.mockTwilioAuthenticator = mockTwilioAuthenticator;
    }

}
