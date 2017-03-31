package com.twilio.authsample;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

import com.twilio.authsample.mocks.TestApp;

/**
 * Created by jsuarez on 5/31/16.
 */
public class TwilioAuthSampleInstrumentationRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, TestApp.class.getName(), context);
    }
}
