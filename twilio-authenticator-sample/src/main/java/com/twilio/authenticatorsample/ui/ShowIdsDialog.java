package com.twilio.authenticatorsample.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.twilio.authenticatorsample.R;

/**
 * Created by jsuarez on 12/20/17.
 */

public class ShowIdsDialog extends DialogFragment {

    private static final String AUTHY_ID = "authy_id";
    private static final String DEVICE_ID = "device_id";

    public static ShowIdsDialog create(String authyId, String deviceId) {
        ShowIdsDialog showIdsDialog = new ShowIdsDialog();
        Bundle args = new Bundle();
        args.putString(AUTHY_ID, authyId);
        args.putString(DEVICE_ID, deviceId);

        showIdsDialog.setArguments(args);

        return showIdsDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String message = getString(R.string.dialog_show_ids, getDeviceId(), getAuthyId());
        builder.setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private String getAuthyId() {
        return getArguments().getString(AUTHY_ID);
    }

    private String getDeviceId() {
        return getArguments().getString(DEVICE_ID);
    }
}
