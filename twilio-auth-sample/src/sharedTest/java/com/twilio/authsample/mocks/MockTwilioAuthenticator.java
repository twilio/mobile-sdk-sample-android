package com.twilio.authsample.mocks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.authy.commonandroid.external.TwilioException;
import com.twilio.authenticator.TwilioAuthenticator;
import com.twilio.authenticator.TwilioAuthenticatorTaskCallback;
import com.twilio.authenticator.external.ApprovalRequest;
import com.twilio.authenticator.external.ApprovalRequestStatus;
import com.twilio.authenticator.external.ApprovalRequests;
import com.twilio.authenticator.external.AuthenticatorToken;
import com.twilio.authenticator.external.TOTPCallback;
import com.twilio.authenticator.external.TOTPs;
import com.twilio.authenticator.external.TimeInterval;

import java.util.List;

/**
 * Created by jsuarez on 5/12/16.
 */
public class MockTwilioAuthenticator implements TwilioAuthenticator {

    private boolean registered;
    private boolean errorOnUpdate;
    private ApprovalRequests approvalRequests;
    private TOTPs totps;
    private List<AuthenticatorToken> apps;

    public MockTwilioAuthenticator(boolean registered) {
        this.registered = registered;
    }

    @Override
    public boolean isDeviceRegistered() {
        return registered;
    }

    @Override
    public void getApprovalRequests(List<ApprovalRequestStatus> statusList,
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
    public void getTOTPs(@NonNull TOTPCallback totpCallback) {
        if (totps == null || totps.isEmpty()) {
            totpCallback.onTOTPError(new Exception("Test exception, invalid TOTP"));
            return;
        }

        totpCallback.onTOTPReceived(totps);
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

    public void setApps(List<AuthenticatorToken> apps) {
        this.apps = apps;
    }

    @Override
    public List<AuthenticatorToken> getApps() {
        return this.apps;
    }

    public void setTotps(TOTPs totps) {
        this.totps = totps;
    }
}
