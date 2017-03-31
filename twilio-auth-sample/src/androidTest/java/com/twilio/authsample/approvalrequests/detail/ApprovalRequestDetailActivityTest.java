package com.twilio.authsample.approvalrequests.detail;

import android.content.Intent;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.twilio.auth.external.ApprovalRequestStatus;
import com.twilio.authsample.R;
import com.twilio.authsample.approvalrequests.ApprovalRequestsListActivityTest;
import com.twilio.authsample.matchers.RecyclerViewItemCountAssertion;
import com.twilio.authsample.matchers.RecyclerViewItemMatcher;
import com.twilio.authsample.matchers.ToolbarTitleMatcher;
import com.twilio.authsample.mocks.MockApprovalRequest;
import com.twilio.authsample.mocks.MockTwilioAuth;
import com.twilio.authsample.mocks.TestApp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.HashMap;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * Created by jsuarez on 6/2/16.
 */
@RunWith(AndroidJUnit4.class)
public class ApprovalRequestDetailActivityTest {

    public static final String DETAILS_DATE_NAME = "Date";
    public static final String DETAILS_DATE_VALUE = "May 28, 2016 8:00AM";
    @Rule
    public ActivityTestRule<ApprovalRequestDetailActivity> approvalRequestDetailActivityTestRule =
            new ActivityTestRule<>(ApprovalRequestDetailActivity.class, false, false);

    private MockTwilioAuth mockAuthySdk;
    private MockApprovalRequest.Builder builder;

    @Before
    public void setUp() throws Exception {
        TestApp application = TestApp.TEST_APP;
        mockAuthySdk = new MockTwilioAuth(application, true);
        application.setMockAuthy(mockAuthySdk);

        builder = new MockApprovalRequest.Builder();
        builder.setMessage(ApprovalRequestsListActivityTest.APPROVAL_REQUEST_PENDING_MESSAGE);
        builder.setTransactionId("uuid1");
        builder.setStatus(ApprovalRequestStatus.pending);
        builder.setCreationDate(new Date());
        HashMap<String, String> details = new HashMap<>();
        details.put(DETAILS_DATE_NAME, DETAILS_DATE_VALUE);
        builder.setDetails(details);
    }

    @Test
    public void testDetailInfo() throws Exception {
        Intent approvalRequestDetailIntent = ApprovalRequestDetailActivity.createIntent(getTargetContext(), builder.createMockApprovalRequest());
        approvalRequestDetailActivityTestRule.launchActivity(approvalRequestDetailIntent);

        // Check activity title
        CharSequence approvalRequestDetailActivityTitle = getTargetContext().getString(R.string.title_activity_approval_request_detail);
        onView(withId(R.id.action_bar)).check(matches(new ToolbarTitleMatcher(is(approvalRequestDetailActivityTitle))));

        // Check transaction message
        onView(withId(R.id.transactionMessage)).check(matches(withText(ApprovalRequestsListActivityTest.APPROVAL_REQUEST_PENDING_MESSAGE)));

        // Check details are displayed
        onView(withId(R.id.approvalRequestAttributes)).check(new RecyclerViewItemCountAssertion(1));
        onView(withId(R.id.approvalRequestAttributes))
                .perform(scrollToPosition(0))
                .check(matches(allOf(hasDescendant(withId(R.id.info_value)), new RecyclerViewItemMatcher(0, hasDescendant(withText(DETAILS_DATE_VALUE))))));

        onView(withId(R.id.approvalRequestAttributes))
                .perform(scrollToPosition(0))
                .check(matches(allOf(hasDescendant(withId(R.id.info_key)), new RecyclerViewItemMatcher(0, hasDescendant(withText(DETAILS_DATE_NAME + ":"))))));

        // Check that buttons are displayed
        onView(withId(R.id.approveButton)).check(matches(isDisplayed()));
        onView(withId(R.id.denyButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testApproveButton() throws Exception {
        Intent approvalRequestDetailIntent = ApprovalRequestDetailActivity.createIntent(getTargetContext(), builder.createMockApprovalRequest());
        approvalRequestDetailActivityTestRule.launchActivity(approvalRequestDetailIntent);

        // Check that button is displayed
        onView(withId(R.id.approveButton)).check(matches(isDisplayed()));

        // Check that the correct error is displayed
        mockAuthySdk.setErrorOnUpdate(true);

        onView(withId(R.id.approveButton)).perform(click());
        // Error message is displayed
        onView(withId(android.support.design.R.id.snackbar_text))
                .check(matches(isDisplayed()));

        // Check that the user is taken back to the previous activity
        // when the request is approved successfully
        mockAuthySdk.setErrorOnUpdate(false);

        // Check that the activity is finishing after the message disappears
        final ApprovalRequestDetailActivity approvalRequestDetailActivity = approvalRequestDetailActivityTestRule.getActivity();
        approvalRequestDetailActivity.messageHelper.getSnackbar().addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                assertTrue(approvalRequestDetailActivity.isFinishing());
            }
        });

        onView(withId(R.id.approveButton)).perform(click());
    }

    @Test
    public void testDenyButton() throws Exception {
        Intent approvalRequestDetailIntent = ApprovalRequestDetailActivity.createIntent(getTargetContext(), builder.createMockApprovalRequest());
        approvalRequestDetailActivityTestRule.launchActivity(approvalRequestDetailIntent);

        // Check that button is displayed
        onView(withId(R.id.denyButton)).check(matches(isDisplayed()));

        // Check that the correct error is displayed
        mockAuthySdk.setErrorOnUpdate(true);

        onView(withId(R.id.denyButton)).perform(click());
        // Error message is displayed
        onView(withId(android.support.design.R.id.snackbar_text))
                .check(matches(isDisplayed()));

        // Check that the user is taken back to the previous activity
        // when the request is denied successfully
        mockAuthySdk.setErrorOnUpdate(false);

        // Check that the activity is finishing after the message disappears
        final ApprovalRequestDetailActivity approvalRequestDetailActivity = approvalRequestDetailActivityTestRule.getActivity();
        approvalRequestDetailActivity.messageHelper.getSnackbar().addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                assertTrue(approvalRequestDetailActivity.isFinishing());
            }
        });

        onView(withId(R.id.denyButton)).perform(click());
    }

    @Test
    public void testApprovedRequest() {
        builder.setStatus(ApprovalRequestStatus.approved);
        Intent approvalRequestDetailIntent = ApprovalRequestDetailActivity.createIntent(getTargetContext(), builder.createMockApprovalRequest());
        approvalRequestDetailActivityTestRule.launchActivity(approvalRequestDetailIntent);

        // Check that buttons are not displayed
        onView(withId(R.id.approveButton)).check(matches(not(isDisplayed())));
        onView(withId(R.id.denyButton)).check(matches(not(isDisplayed())));

        // Check that the approved message is displayed
        onView(withId(R.id.transactionStatusMessage)).check(matches(withText(R.string.transaction_approved_message)));
        onView(withId(R.id.transactionWarning)).check(matches(withText(R.string.transaction_not_updatable_warning)));

    }

    @Test
    public void testDeniedRequest() {
        builder.setStatus(ApprovalRequestStatus.denied);
        Intent approvalRequestDetailIntent = ApprovalRequestDetailActivity.createIntent(getTargetContext(), builder.createMockApprovalRequest());
        approvalRequestDetailActivityTestRule.launchActivity(approvalRequestDetailIntent);

        // Check that buttons are not displayed
        onView(withId(R.id.approveButton)).check(matches(not(isDisplayed())));
        onView(withId(R.id.denyButton)).check(matches(not(isDisplayed())));

        // Check that the approved message is displayed
        onView(withId(R.id.transactionStatusMessage)).check(matches(withText(R.string.transaction_denied_message)));
        onView(withId(R.id.transactionWarning)).check(matches(withText(R.string.transaction_not_updatable_warning)));
    }

    @Test
    public void testExpiredRequest() {
        builder.setStatus(ApprovalRequestStatus.expired);
        builder.setExpirationTimestamp(new Date().getTime());
        Intent approvalRequestDetailIntent = ApprovalRequestDetailActivity.createIntent(getTargetContext(), builder.createMockApprovalRequest());
        approvalRequestDetailActivityTestRule.launchActivity(approvalRequestDetailIntent);

        // Check that buttons are not displayed
        onView(withId(R.id.approveButton)).check(matches(not(isDisplayed())));
        onView(withId(R.id.denyButton)).check(matches(not(isDisplayed())));

        // Check that the approved message is displayed
        String expiredStatusMessage = getTargetContext().getString(R.string.transaction_expired_message);
        expiredStatusMessage = expiredStatusMessage.replace("%1$s","");
        onView(withId(R.id.transactionStatusMessage)).check(matches(withText(containsString(expiredStatusMessage))));
        onView(withId(R.id.transactionWarning)).check(matches(withText(R.string.transaction_not_updatable_warning)));
    }
}