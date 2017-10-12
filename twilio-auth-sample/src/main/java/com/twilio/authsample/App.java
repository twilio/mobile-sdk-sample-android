package com.twilio.authsample;

import android.app.Application;

import com.squareup.otto.Bus;
import com.twilio.authenticator.TwilioAuthenticator;

/**
 * Created by jsuarez on 3/11/16.
 */
public class App extends Application {

    private TwilioAuthenticator twilioAuthenticator;
    private Bus bus;

    @Override
    public void onCreate() {
        super.onCreate();

        twilioAuthenticator = buildTwilioAuthenticator();

        bus = new Bus();
    }

    protected TwilioAuthenticator buildTwilioAuthenticator() {
        return TwilioAuthenticator.Instance.getInstance(this);
    }

    public TwilioAuthenticator getTwilioAuthenticator() {
        return twilioAuthenticator;
    }

    public Bus getBus() {
        return bus;
    }
}
