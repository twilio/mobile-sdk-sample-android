package com.twilio.authsample.utils;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Created by jsuarez on 11/22/16.
 */

public abstract class AuthyTask<T> extends AsyncTask<Void, Void, T> {

    private final WeakReference<AuthyActivityListener<T>> authyActivityListenerRefs;
    private Exception exception;

    public AuthyTask(AuthyActivityListener<T> authyActivityListener) {
        this.authyActivityListenerRefs = new WeakReference<>(authyActivityListener);
    }

    @Override
    protected T doInBackground(Void... voids) {
        try {
            return executeOnBackground();
        } catch (Exception e) {
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(T t) {
        super.onPostExecute(t);

        AuthyActivityListener<T> listener = authyActivityListenerRefs.get();

        if (listener == null) {
            return;
        }

        if (exception == null) {
            listener.onSuccess(t);
            return;
        }

        listener.onError(exception);
    }

    public abstract T executeOnBackground();
}
