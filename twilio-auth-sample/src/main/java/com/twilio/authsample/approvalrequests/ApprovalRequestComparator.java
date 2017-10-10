package com.twilio.authsample.approvalrequests;

import com.twilio.authenticator.external.ApprovalRequest;

import java.util.Comparator;
import java.util.Date;

/**
 * Compares two objects by date with the added case that if {@code a} is an {@link ApprovalRequest} then
 * {@code null < a} for all {@code a} and {@code compare(null,null) == 0}.
 */
public class ApprovalRequestComparator implements Comparator<ApprovalRequest> {

    @Override
    public int compare(ApprovalRequest lhs, ApprovalRequest rhs) {

        if (lhs == null && rhs == null) {
            return 0;
        }

        if (lhs == null) {
            return -1;
        }

        if (rhs == null) {
            return 1;
        }

        Date lhsDate = lhs.getCreationDate();
        Date rhsDate = rhs.getCreationDate();

        if (lhsDate.getTime() == rhsDate.getTime()) {
            return rhs.getUuid().compareTo(lhs.getUuid());
        }
        return rhsDate.compareTo(lhsDate);
    }
}
