package com.twilio.authenticatorsample.mocks;

import com.twilio.authenticator.external.App;

/**
 * Created by lvidal on 10/17/17.
 */

public class MockApp implements App {
    private String name;
    private long appId;

    public MockApp(long appId, String name) {
        this.appId = appId;
        this.name = name;
    }


    @Override
    public long getId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    @Override

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public long getExpirationDate() {
        return 0;
    }

    @Override
    public int getCodeDurationInSeconds() {
        return 0;
    }

    @Override
    public String getCurrentCode() {
        return "1111111";
    }
}
