package com.twilio.authsample.mocks;

import com.twilio.authenticator.external.ApprovalRequest;
import com.twilio.authenticator.external.ApprovalRequests;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lvidal on 10/13/17.
 */
public class MockApprovalRequests extends ApprovalRequests {
    private List<ApprovalRequest> pending = new ArrayList<>();
    private List<ApprovalRequest> approved = new ArrayList<>();
    private List<ApprovalRequest> expired = new ArrayList<>();
    private List<ApprovalRequest> denied = new ArrayList<>();

    @Override
    public List<ApprovalRequest> getApproved() {
        return approved;
    }

    @Override
    public List<ApprovalRequest> getDenied() {
        return denied;
    }

    @Override
    public List<ApprovalRequest> getExpired() {
        return expired;
    }

    @Override
    public List<ApprovalRequest> getPending() {
        return pending;
    }
}
