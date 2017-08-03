package com.twilio.authsample.approvalrequests.events;

/**
 * This class represents a change on the ApprovalRequests or on the
 * filters used by each fragment.
 * The actual changes are stored by the RequestsFragment, this just notifies subscribers to
 * fetch a new value
 * <p/>
 * Created by jsuarez on 4/22/16.
 */
public class ApprovalRequestsUpdatedEvent {

    /**
     * Flag to indicate that a network called was finished
     */
    private final boolean networkFetchFinished;

    public ApprovalRequestsUpdatedEvent(boolean networkFetchFinished) {
        this.networkFetchFinished = networkFetchFinished;
    }

    public boolean isNetworkFetchFinished() {
        return networkFetchFinished;
    }
}
