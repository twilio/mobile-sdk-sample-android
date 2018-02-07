package com.twilio.authenticatorsample.network;

import com.twilio.authenticatorsample.network.model.RegistrationTokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by jsuarez on 2/22/17.
 */

public interface SampleApi {

    @POST("registration")
    @FormUrlEncoded
    Call<RegistrationTokenResponse> getRegistrationToken(@Field("user_id") String userId);
}
