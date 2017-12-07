package com.twilio.authsample.mocks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.authy.commonandroid.external.TwilioException;
import com.twilio.auth.TwilioAuth;
import com.twilio.auth.external.ApprovalRequest;
import com.twilio.auth.external.ApprovalRequestStatus;
import com.twilio.auth.external.ApprovalRequests;
import com.twilio.auth.external.TOTPCallback;
import com.twilio.auth.external.TimeInterval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jsuarez on 5/12/16.
 */
public class MockTwilioAuth extends TwilioAuth {

    private boolean registered;
    private boolean errorOnUpdate;
    private ApprovalRequests approvalRequests;
    private String deviceId = "test_device_id";
    private String totp;
    private Map<String, ApprovalRequest> approvalRequestMap = new HashMap<>();

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

    @Override
    public void getTOTP(TOTPCallback totpCallback) {
        if (TextUtils.isEmpty(totp)) {
            totpCallback.onTOTPError(new Exception("Test exception, invalid TOTP"));
            return;
        }

        totpCallback.onTOTPReceived(totp);
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTotp() {
        return totp;
    }

    public void setTotp(String totp) {
        this.totp = totp;
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
        if (approvalRequests != null) {
            ArrayList<ApprovalRequest> approvalRequestsList = new ArrayList<>();
            approvalRequestsList.addAll(approvalRequests.getApproved());
            approvalRequestsList.addAll(approvalRequests.getDenied());
            approvalRequestsList.addAll(approvalRequests.getExpired());
            approvalRequestsList.addAll(approvalRequests.getPending());
            for (ApprovalRequest request : approvalRequestsList) {
                addApprovalRequest(request);
            }
        }
    }

    @Override
    public ApprovalRequest getRequest(String uuid) {
        return approvalRequestMap.get(uuid);
    }

    public void addApprovalRequest(ApprovalRequest approvalRequest) {
        approvalRequestMap.put(approvalRequest.getUuid(), approvalRequest);
    }
}
