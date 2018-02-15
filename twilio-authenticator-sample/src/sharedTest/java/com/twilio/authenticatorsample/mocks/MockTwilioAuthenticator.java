package com.twilio.authenticatorsample.mocks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.authy.commonandroid.external.TwilioException;
import com.twilio.authenticator.TwilioAuthenticator;
import com.twilio.authenticator.TwilioAuthenticatorTaskCallback;
import com.twilio.authenticator.external.App;
import com.twilio.authenticator.external.ApprovalRequest;
import com.twilio.authenticator.external.ApprovalRequestStatus;
import com.twilio.authenticator.external.ApprovalRequests;
import com.twilio.authenticator.external.MultiAppListener;
import com.twilio.authenticator.external.SingleAppListener;
import com.twilio.authenticator.external.TimeInterval;

import java.util.List;

/**
 * Created by jsuarez on 5/12/16.
 */
public class MockTwilioAuthenticator implements TwilioAuthenticator {

    private boolean registered;
    private boolean errorOnUpdate;
    private ApprovalRequests approvalRequests;
    private List<App> apps;
    private MultiAppListener multiAppListener;
    private SingleAppListener singleAppListener ;


    public MockTwilioAuthenticator(boolean registered) {
        this.registered = registered;
    }

    @Override
    public boolean isDeviceRegistered() {
        return registered;
    }

    @Override
    public void getApprovalRequests(@Nullable final Long appId,
                                    List<ApprovalRequestStatus> statusList,
                                    TimeInterval timeInterval,
                                    @NonNull TwilioAuthenticatorTaskCallback<ApprovalRequests> callback) {
        callback.onSuccess(getApprovalRequests());
    }

    @Override
    public void setPushToken(@NonNull String pushToken,
                @NonNull TwilioAuthenticatorTaskCallback<Void> callback) {
        callback.onSuccess(null);
    }

    @Override
    public void registerDevice(@NonNull String registrationToken,
                               @Nullable String pushToken,
                               @NonNull TwilioAuthenticatorTaskCallback<Void> callback) {
        callback.onSuccess(null);
    }

    @Override
    public void approveRequest(@NonNull ApprovalRequest approvalRequest,
                               @NonNull TwilioAuthenticatorTaskCallback<Void> callback) {
        if (isErrorOnUpdate()) {
            callback.onError(new TwilioException("errorOnUpdate is true",
                    TwilioException.APPROVAL_REQUEST_ERROR));
        } else {
            callback.onSuccess(null);
        }
    }

    @Override
    public void denyRequest(@NonNull ApprovalRequest approvalRequest,
                            @NonNull TwilioAuthenticatorTaskCallback<Void> callback) {
        if (isErrorOnUpdate()) {
            callback.onError(new TwilioException("errorOnUpdate is true",
                    TwilioException.APPROVAL_REQUEST_ERROR));
        } else {
            callback.onSuccess(null);
        }
    }

    @Override
    public void clearLocalData() {
        this.registered = false;
    }

    @Override
    public void setMultiAppListener(@NonNull MultiAppListener multiAppListener) {
        this.multiAppListener = multiAppListener;
        notifyListener();
    }

    @Override
    public void setSingleAppListener(@NonNull SingleAppListener singleAppListener) {
        this.singleAppListener = singleAppListener;
        notifyListener();
    }


    private void notifyListener() {
        if (multiAppListener != null) {
            multiAppListener.onNewCode(apps);
        }

        if (singleAppListener != null) {
            singleAppListener.onNewCode(apps.get(0));
        }
    }


    @Override
    public void getApprovalRequest(@NonNull String uuid,
                                   @NonNull TwilioAuthenticatorTaskCallback<ApprovalRequest> callback) {
        callback.onSuccess(getApprovalRequests().getApprovalRequestById(uuid));
    }

    @Override
    public String getDeviceId() {
        return "test_device_id";
    }

    @Override
    public String getAuthyId() {
        return "test_authy_id";
    }

    private boolean isErrorOnUpdate() {
        return errorOnUpdate;
    }

    public void setErrorOnUpdate(boolean errorOnUpdate) {
        this.errorOnUpdate = errorOnUpdate;
    }

    public ApprovalRequests getApprovalRequests() {
        if (approvalRequests == null) {
            approvalRequests = new ApprovalRequests();
        }

        return approvalRequests;
    }

    public void setApprovalRequests(ApprovalRequests approvalRequests) {
        this.approvalRequests = approvalRequests;
    }

    public void setApps(List<App> apps) {
        this.apps = apps;
        notifyListener();
    }
}
