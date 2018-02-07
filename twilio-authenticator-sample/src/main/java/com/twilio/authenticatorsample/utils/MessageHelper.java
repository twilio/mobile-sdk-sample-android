package com.twilio.authenticatorsample.utils;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by jsuarez on 5/11/16.
 */
public class MessageHelper {

    private Snackbar snackbar;

    /**
     * Shows a message if it is not already showing
     *
     * @param anchorView
     * @param messageId
     */
    public Snackbar show(@NonNull View anchorView, @StringRes int messageId) {
        return show(anchorView, anchorView.getContext().getString(messageId));
    }

    /**
     * Shows a message if it is not already showing
     *
     * @param anchorView
     * @param message
     */
    public Snackbar show(@NonNull View anchorView, String message) {
        if (getSnackbar() == null) {
            snackbar = Snackbar.make(anchorView, message, Snackbar.LENGTH_LONG);
        } else {
            getSnackbar().setText(message);
        }

        if (!getSnackbar().isShown()) {
            getSnackbar().show();
        }

        return getSnackbar();
    }

    /**
     * @return current Snackbar or null
     */
    public Snackbar getSnackbar() {
        return snackbar;
    }

    /**
     * Dismiss message
     */
    public void dismiss() {
        if (snackbar != null) {
            snackbar.dismiss();
            snackbar = null;
        }
    }

    public void removeCallback(Snackbar.Callback callback) {
        if (snackbar != null) {
            snackbar.removeCallback(callback);
        }
    }
}
