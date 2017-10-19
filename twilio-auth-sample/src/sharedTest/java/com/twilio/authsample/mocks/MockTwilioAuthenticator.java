package com.twilio.authsample.mocks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.authy.commonandroid.external.TwilioException;
import com.twilio.authenticator.TwilioAuthenticator;
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
    private String deviceId = "test_device_id";
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
    public ApprovalRequests getApprovalRequests(List<ApprovalRequestStatus> statusList, TimeInterval timeInterval) {
        return getApprovalRequests();
    }

    @Override
    public void setPushToken(String pushToken) {

    }

    @Override
    public void registerDevice(@NonNull String registrationToken, @Nullable String pushToken, @Nullable String integrationApiKey) {
        return;
    }

    @Override
    public void approveRequest(@NonNull ApprovalRequest approvalRequest) {
        if (isErrorOnUpdate()) {
            throw new TwilioException("errorOnUpdate is true", TwilioException.APPROVAL_REQUEST_ERROR);
        }
    }

    @Override
    public void denyRequest(@NonNull ApprovalRequest approvalRequest) {
        if (isErrorOnUpdate()) {
            throw new TwilioException("errorOnUpdate is true", TwilioException.APPROVAL_REQUEST_ERROR);
        }
    }

    @Override
    public void clearLocalData() {
        this.registered = false;
    }

    @Override
    public void getTOTP(TOTPCallback totpCallback) {
        if (totps == null || totps.isEmpty()) {
            totpCallback.onTOTPError(new Exception("Test exception, invalid TOTP"));
            return;
        }

        totpCallback.onTOTPReceived(totps);
    }

    @Override
    public ApprovalRequest getRequest(String uuid) {
        return getApprovalRequests().getApprovalRequestById(uuid);
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isErrorOnUpdate() {
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
}
