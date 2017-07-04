package com.twilio.authsample.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.authy.commonandroid.external.TwilioException;
import com.twilio.auth.TwilioAuth;
import com.twilio.auth.external.ApprovalRequest;
import com.twilio.auth.external.ApprovalRequests;
import com.twilio.authsample.App;
import com.twilio.authsample.R;
import com.twilio.authsample.approvalrequests.detail.ApprovalRequestDetailActivity;
import com.twilio.authsample.approvalrequests.events.RefreshApprovalRequestsEvent;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = MessagingService.class.getSimpleName();
    public static final String ONETOUCH_APPROVAL_REQUEST_TYPE = "onetouch_approval_request";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            if (ONETOUCH_APPROVAL_REQUEST_TYPE.equals(remoteMessage.getData().get("type"))) {
                sendNotification(remoteMessage.getData());
            }

        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageData FCM message body received.
     */
    private void sendNotification(Map<String, String> messageData) {

        // Get the approval request id
        String approvalRequestUuid = messageData.get("approval_request_uuid");

        TwilioAuth twilioAuth = ((App) getApplicationContext()).getTwilioAuth();

        if (!twilioAuth.isDeviceRegistered()) {
            Log.d(TAG, "Device not registered");
            return;
        }

        ApprovalRequest approvalRequest = getApprovalRequestFromId(twilioAuth, approvalRequestUuid);
        if (approvalRequest == null) {
            Log.d(TAG, "ApprovalRequest not found");
            return;
        }

        // Create task of activities to be opened when user clicks on the notification
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(ApprovalRequestDetailActivity.class);
        // Adds the ApprovalRequestDetailActivity to the top of the stack
        Intent intent = ApprovalRequestDetailActivity.createIntent(this, approvalRequest);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.default_logo)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageData.get("alert"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

        // Update the main views if visible
        sendEventToUpdateUI();
    }

    private void sendEventToUpdateUI() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                ((App) getApplicationContext()).getBus().post(new RefreshApprovalRequestsEvent());
            }
        });
    }

    private ApprovalRequest getApprovalRequestFromId(TwilioAuth twilioAuth, String approvalRequestUuid) {
        try {
            ApprovalRequests approvalRequests = twilioAuth.getApprovalRequests(
                    null,
                    null);

            return approvalRequests.getApprovalRequestById(approvalRequestUuid);
        } catch (TwilioException e) {
            return null;
        }
    }

}
