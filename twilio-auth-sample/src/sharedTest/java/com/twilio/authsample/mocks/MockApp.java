package com.twilio.authsample.mocks;

import com.twilio.authenticator.external.AuthenticatorToken;
import com.twilio.authenticator.external.TOTP;

/**
 * Created by lvidal on 10/17/17.
 */

public class MockApp implements AuthenticatorToken {
    public MockApp(String appId, String name) {
        this.appId = appId;
        this.name = name;
    }

    private String appId;

    @Override
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    @Override
    public TOTP getTotp() {
        return new TOTP() {
            @Override
            public String getToken() {
                return "1111111";
            }

            @Override
            public long getExpiresInMillis() {
                return 0;
            }

            @Override
            public long getUpdateIntervalMillis() {
                return 0;
            }
        };
    }
}
