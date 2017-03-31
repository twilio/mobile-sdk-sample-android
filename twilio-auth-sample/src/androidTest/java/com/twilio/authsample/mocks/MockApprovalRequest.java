package com.twilio.authsample.mocks;

import android.os.Build;

import com.twilio.auth.external.ApprovalRequest;
import com.twilio.auth.external.ApprovalRequestStatus;
import com.twilio.auth.external.ApprovalRequestLogo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jsuarez on 6/1/16.
 */
public class MockApprovalRequest implements ApprovalRequest {
    private final long expirationTimeStamp;
    private Date signingTime;
    private Date creationDate;
    private String transactionId;
    private ApprovalRequestStatus status;
    private String message;
    private final Map<String, String> details;

    public MockApprovalRequest(String transactionId, ApprovalRequestStatus status, String message, Map<String, String> details, Date creationDate, long expirationTimeStamp) {
        this.transactionId = transactionId;
        this.status = status;
        this.message = message;
        this.details = details;
        this.creationDate = creationDate;
        this.expirationTimeStamp = expirationTimeStamp;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Map<String, String> getDetails() {
        return details;
    }

    @Override
    public Map<String, String> getHiddenDetails() {
        return null;
    }

    @Override
    public Map<String, String> getRequesterDetails() {
        return null;
    }

    @Override
    public Map<String, String> getDeviceDetails() {
        return null;
    }

    @Override
    public void setDeviceDetails(Map<String, String> deviceInfo) {

    }

    @Override
    public Date getSigningTime() {
        return signingTime;
    }

    @Override
    public void setSigningTime(Date date) {
        this.signingTime = date;
    }

    @Override
    public String getLocation() {
        return null;
    }

    @Override
    public void setLocation(String location) {

    }

    @Override
    public String getUuid() {
        return transactionId;
    }

    @Override
    public Long getCustomerUuid() {
        return null;
    }

    @Override
    public String getDeviceUuid() {
        return Build.SERIAL;
    }

    @Override
    public Date getCreationDate() {
        if (creationDate == null) {
            creationDate = new Date();
        }
        return creationDate;
    }

    @Override
    public ApprovalRequestStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(ApprovalRequestStatus status) {
        this.status = status;
    }

    @Override
    public String getReason() {
        return null;
    }

    @Override
    public long getExpirationTimestamp() {
        return expirationTimeStamp;
    }

    @Override
    public void setReason(String reason) {

    }

    @Override
    public boolean isFlagged() {
        return false;
    }

    @Override
    public void setFlagged(boolean flagged) {

    }

    @Override
    public boolean isEncrypted() {
        return false;
    }

    @Override
    public List<? extends ApprovalRequestLogo> getLogos() {
        return new ArrayList<>();
    }

    @Override
    public boolean isPending() {
        return false;
    }

    @Override
    public boolean isApproved() {
        return false;
    }

    @Override
    public boolean isDenied() {
        return false;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    public static class Builder {
        private String transactionId;
        private ApprovalRequestStatus status;
        private String message = "";
        private Date creationDate = new Date();
        private Map<String, String> details = new HashMap<>();
        private long expirationTimeStamp;

        public Builder setTransactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder setStatus(ApprovalRequestStatus status) {
            this.status = status;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setCreationDate(Date creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder setExpirationTimestamp(long expirationTimeStamp) {
            this.expirationTimeStamp = expirationTimeStamp;
            return this;
        }

        public Builder setDetails(Map<String, String> details) {
            this.details = details;
            return this;
        }

        public MockApprovalRequest createMockApprovalRequest() {
            return new MockApprovalRequest(transactionId, status, message, details, creationDate, expirationTimeStamp);
        }
    }
}
