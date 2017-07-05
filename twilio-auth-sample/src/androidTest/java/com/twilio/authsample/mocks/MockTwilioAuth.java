package com.twilio.authsample.mocks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.authy.commonandroid.external.TwilioException;
import com.twilio.auth.TwilioAuth;
import com.twilio.auth.external.ApprovalRequest;
import com.twilio.auth.external.ApprovalRequestStatus;
import com.twilio.auth.external.ApprovalRequests;
import com.twilio.auth.external.TimeInterval;

import java.util.List;

/**
 * Created by jsuarez on 5/12/16.
 */
public class MockTwilioAuth extends TwilioAuth {

    private boolean registered;
    private boolean errorOnUpdate;
    private ApprovalRequests approvalRequests;

    public MockTwilioAuth(Context context, boolean registered) {
        super(context);
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
    public void registerDevice(@NonNull String registrationToken, @Nullable String pushToken) {
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

    public boolean isErrorOnUpdate() {
        return errorOnUpdate;
    }

    public void setErrorOnUpdate(boolean errorOnUpdate) {
        this.errorOnUpdate = errorOnUpdate;
    }

    public void setApprovalRequests(ApprovalRequests approvalRequests) {
        this.approvalRequests = approvalRequests;
    }

    public ApprovalRequests getApprovalRequests() {
        if (approvalRequests == null) {
            approvalRequests = new ApprovalRequests();
        }

        return approvalRequests;
    }
}
