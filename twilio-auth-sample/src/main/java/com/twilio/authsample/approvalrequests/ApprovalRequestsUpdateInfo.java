package com.twilio.authsample.approvalrequests;

import com.twilio.auth.external.ApprovalRequestStatus;
import com.twilio.auth.external.ApprovalRequests;

import java.util.List;

/**
 * Class containing information regarding an approval request update
 * Created by jsuarez on 4/24/16.
 */
public class ApprovalRequestsUpdateInfo {
    private final List<ApprovalRequestStatus> statusList;
    private final ApprovalRequests approvalRequests;
    private final boolean hideStatusInfo;

    public ApprovalRequestsUpdateInfo(final ApprovalRequests approvalRequests, final List<ApprovalRequestStatus> statusList, final boolean hideStatusInfo) {
        this.approvalRequests = approvalRequests;
        this.hideStatusInfo = hideStatusInfo;
        this.statusList = statusList;
    }

    public List<ApprovalRequestStatus> getStatusList() {
        return statusList;
    }

    public boolean isHideStatusInfo() {
        return hideStatusInfo;
    }

    public ApprovalRequests getApprovalRequests() {
        return approvalRequests;
    }
}
