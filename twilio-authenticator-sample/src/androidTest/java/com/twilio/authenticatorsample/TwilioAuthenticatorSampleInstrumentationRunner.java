package com.twilio.authenticatorsample;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

import com.twilio.authenticatorsample.mocks.TestSampleApp;

/**
 * Created by jsuarez on 5/31/16.
 */
public class TwilioAuthenticatorSampleInstrumentationRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, TestSampleApp.class.getName(), context);
    }
}
