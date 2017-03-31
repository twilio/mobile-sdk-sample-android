package com.twilio.authsample.utils;

/**
 * Created by jsuarez on 5/17/16.
 */
public interface AuthyActivityListener<T> {
    void onSuccess(T result);

    void onError(Exception exception);
}
