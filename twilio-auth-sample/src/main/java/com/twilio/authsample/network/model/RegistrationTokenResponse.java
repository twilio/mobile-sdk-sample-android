package com.twilio.authsample.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jsuarez on 2/22/17.
 */

public class RegistrationTokenResponse {
    @SerializedName("registration_token")
    private String registrationToken;

    public String getRegistrationToken() {
        return registrationToken;
    }

    public void setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
    }
}
