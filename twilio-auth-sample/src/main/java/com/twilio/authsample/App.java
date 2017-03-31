package com.twilio.authsample;

import android.app.Application;

import com.twilio.auth.TwilioAuth;
import com.squareup.otto.Bus;

/**
 * Created by jsuarez on 3/11/16.
 */
public class App extends Application {

    private TwilioAuth twilioAuth;
    private Bus bus;

    @Override
    public void onCreate() {
        super.onCreate();

        twilioAuth = TwilioAuth.getInstance(this);

        bus = new Bus();
    }

    public TwilioAuth getTwilioAuth() {
        return twilioAuth;
    }

    public Bus getBus() {
        return bus;
    }
}
