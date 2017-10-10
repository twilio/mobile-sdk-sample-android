package com.twilio.authsample.approvalrequests.detail;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.twilio.authenticator.external.ApprovalRequestStatus;
import com.twilio.authsample.BuildConfig;
import com.twilio.authsample.R;
import com.twilio.authsample.approvalrequests.adapters.ApprovalRequestInfoAdapter;
import com.twilio.authsample.mocks.MockApprovalRequest;
import com.twilio.authsample.mocks.MockTwilioAuthenticator;
import com.twilio.authsample.mocks.TestApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jsuarez on 8/14/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class)
public class ApprovalRequestDetailActivityTest {

    public static final String DETAILS_DATE_NAME = "Date";
    public static final String DETAILS_DATE_VALUE = "May 28, 2016 8:00AM";
    public static final String APPROVAL_REQUEST_PENDING_MESSAGE = "Pending approval request";

    private MockApprovalRequest.Builder builder;
    private TestApp context;
    private Activity activity;
    private MockTwilioAuthenticator mockTwilioAuthenticator;

    @Before
    public void setUp() throws Exception {
        context = (TestApp) RuntimeEnvironment.application;

        mockTwilioAuthenticator = new MockTwilioAuthenticator(context, true);
        context.setTwilioAuth(mockTwilioAuthenticator);

        builder = new MockApprovalRequest.Builder();
        builder.setMessage(APPROVAL_REQUEST_PENDING_MESSAGE);
        builder.setTransactionId("uuid1");
        builder.setStatus(ApprovalRequestStatus.pending);
        builder.setCreationDate(new Date());
        HashMap<String, String> details = new HashMap<>();
        details.put(DETAILS_DATE_NAME, DETAILS_DATE_VALUE);
        builder.setDetails(details);
        Intent requestDetailIntent = ApprovalRequestDetailActivity.createIntent(context, builder.createMockApprovalRequest());
        activity = Robolectric.buildActivity(ApprovalRequestDetailActivity.class, requestDetailIntent).create().get();
    }

    @Test
    public void testDetailInfo() throws Exception {
        // Check activity title
        CharSequence approvalRequestDetailActivityTitle = activity.getString(R.string.title_activity_approval_request_detail);
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.action_bar);
        assertNotNull(toolbar);
        assertEquals("Invalid title", approvalRequestDetailActivityTitle, toolbar.getTitle());

        // Check request message
        TextView requestMessage = (TextView) activity.findViewById(R.id.transactionMessage);
        assertEquals("Invalid message", APPROVAL_REQUEST_PENDING_MESSAGE, requestMessage.getText().toString());

        // Check details are displayed
        RecyclerView attributesList = (RecyclerView) activity.findViewById(R.id.approvalRequestAttributes);
        ApprovalRequestInfoAdapter adapter = (ApprovalRequestInfoAdapter) attributesList.getAdapter();
        assertEquals("Invalid number of items", 1, adapter.getItemCount());
        Map.Entry<String, String> detail = adapter.getItemForPosition(0);
        assertEquals("Invalid value", DETAILS_DATE_NAME, detail.getKey());
        assertEquals("Invalid value", DETAILS_DATE_VALUE, detail.getValue());

        // Check that buttons are displayed
        View approveButton = activity.findViewById(R.id.approveButton);
        assertNotNull("Button must be present", approveButton);
        assertEquals("Button must be visible", View.VISIBLE, approveButton.getVisibility());

        View denyButton = activity.findViewById(R.id.denyButton);
        assertNotNull("Button must be present", denyButton);
        assertEquals("Button must be visible", View.VISIBLE, denyButton.getVisibility());
    }

    @Test
    public void testApproveButton() throws Exception {
        View approveButton = activity.findViewById(R.id.approveButton);
        assertNotNull("Button must be present", approveButton);
        assertEquals("Button must be visible", View.VISIBLE, approveButton.getVisibility());

        // Check that the correct error is displayed
        mockTwilioAuthenticator.setErrorOnUpdate(true);
        approveButton.performClick();

        // Error message is displayed
        TextView snackbar = (TextView) activity.findViewById(android.support.design.R.id.snackbar_text);
        assertNotNull("View can't be null", snackbar);
        assertEquals("Error message is not valid", activity.getString(R.string.approve_failed), snackbar.getText().toString());

        // Check successful approval
        mockTwilioAuthenticator.setErrorOnUpdate(false);
        approveButton.performClick();
        assertEquals("Message is not valid", activity.getString(R.string.approve_success), snackbar.getText().toString());
    }

    @Test
    public void testDenyButton() throws Exception {
        View denyButton = activity.findViewById(R.id.denyButton);
        assertNotNull("Button must be present", denyButton);
        assertEquals("Button must be visible", View.VISIBLE, denyButton.getVisibility());

        // Check that the correct error is displayed
        mockTwilioAuthenticator.setErrorOnUpdate(true);
        denyButton.performClick();

        // Error message is displayed
        TextView snackbar = (TextView) activity.findViewById(android.support.design.R.id.snackbar_text);
        assertNotNull("View can't be null", snackbar);
        assertEquals("Error message is not valid", activity.getString(R.string.deny_failed), snackbar.getText().toString());

        // Check successful approval
        mockTwilioAuthenticator.setErrorOnUpdate(false);
        denyButton.performClick();
        assertEquals("Message is not valid", activity.getString(R.string.deny_success), snackbar.getText().toString());
    }

    @Test
    public void testApprovedRequest() {
        builder.setStatus(ApprovalRequestStatus.approved);
        Intent requestDetailIntent = ApprovalRequestDetailActivity.createIntent(context, builder.createMockApprovalRequest());
        activity = Robolectric.buildActivity(ApprovalRequestDetailActivity.class, requestDetailIntent).create().get();

        // Check that buttons are not enabled
        View approveButton = activity.findViewById(R.id.approveButton);
        assertNotNull("Button must be present", approveButton);
        assertFalse("Button must not be enabled", approveButton.isEnabled());

        View denyButton = activity.findViewById(R.id.denyButton);
        assertNotNull("Button must be present", denyButton);
        assertFalse("Button must not be enabled", denyButton.isEnabled());

        // Check that the approved message is displayed
        TextView statusMessage = (TextView) activity.findViewById(R.id.transactionStatusMessage);
        TextView warning = (TextView) activity.findViewById(R.id.transactionWarning);
        assertEquals("Invalid message", activity.getString(R.string.transaction_approved_message), statusMessage.getText().toString());
        assertEquals("Invalid message", activity.getString(R.string.transaction_not_updatable_warning), warning.getText().toString());
    }

    @Test
    public void testDeniedRequest() {
        builder.setStatus(ApprovalRequestStatus.denied);
        Intent requestDetailIntent = ApprovalRequestDetailActivity.createIntent(context, builder.createMockApprovalRequest());
        activity = Robolectric.buildActivity(ApprovalRequestDetailActivity.class, requestDetailIntent).create().get();

        // Check that buttons are not enabled
        View approveButton = activity.findViewById(R.id.approveButton);
        assertNotNull("Button must be present", approveButton);
        assertFalse("Button must not be enabled", approveButton.isEnabled());

        View denyButton = activity.findViewById(R.id.denyButton);
        assertNotNull("Button must be present", denyButton);
        assertFalse("Button must not be enabled", denyButton.isEnabled());

        // Check that the denied message is displayed
        TextView statusMessage = (TextView) activity.findViewById(R.id.transactionStatusMessage);
        TextView warning = (TextView) activity.findViewById(R.id.transactionWarning);
        assertEquals("Invalid message", activity.getString(R.string.transaction_denied_message), statusMessage.getText().toString());
        assertEquals("Invalid message", activity.getString(R.string.transaction_not_updatable_warning), warning.getText().toString());
    }

    @Test
    public void testExpiredRequest() {
        builder.setStatus(ApprovalRequestStatus.expired);
        builder.setExpirationTimestamp(new Date().getTime());
        Intent requestDetailIntent = ApprovalRequestDetailActivity.createIntent(context, builder.createMockApprovalRequest());
        activity = Robolectric.buildActivity(ApprovalRequestDetailActivity.class, requestDetailIntent).create().get();

        // Check that buttons are not enabled
        View approveButton = activity.findViewById(R.id.approveButton);
        assertNotNull("Button must be present", approveButton);
        assertFalse("Button must not be enabled", approveButton.isEnabled());

        // Check that the expired message is displayed
        String expiredStatusMessage = activity.getString(R.string.transaction_expired_message);
        expiredStatusMessage = expiredStatusMessage.replace("%1$s", "");
        TextView statusMessage = (TextView) activity.findViewById(R.id.transactionStatusMessage);
        TextView warning = (TextView) activity.findViewById(R.id.transactionWarning);
        assertTrue("Invalid message", statusMessage.getText().toString().contains(expiredStatusMessage));
        assertEquals("Invalid message", activity.getString(R.string.transaction_not_updatable_warning), warning.getText().toString());
    }
}