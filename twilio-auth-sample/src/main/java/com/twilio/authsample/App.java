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

        twilioAuthenticator = TwilioAuthenticator.getInstance(this);

        bus = new Bus();
    }

    public TwilioAuthenticator getTwilioAuthenticator() {
        return twilioAuthenticator;
    }

    public Bus getBus() {
        return bus;
    }
}
