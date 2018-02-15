package com.twilio.authenticatorsample.apps;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.twilio.authenticator.external.App;
import com.twilio.authenticatorsample.R;
import com.twilio.authenticatorsample.matchers.RecyclerViewItemCountAssertion;
import com.twilio.authenticatorsample.matchers.ToolbarTitleMatcher;
import com.twilio.authenticatorsample.mocks.MockApp;
import com.twilio.authenticatorsample.mocks.MockTwilioAuthenticator;
import com.twilio.authenticatorsample.mocks.TestSampleApp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;

/**
 * Created by jsuarez on 6/1/16.
 */
@RunWith(AndroidJUnit4.class)
public class AppsActivityTest {

    @Rule
    public ActivityTestRule<AppsActivity> appsActivityTestRule =
            new ActivityTestRule<>(AppsActivity.class, false, false);

    private MockTwilioAuthenticator mockTwilioAuthenticator;

    @Before
    public void setUp() throws Exception {
        TestSampleApp application = TestSampleApp.TEST_APP;
        mockTwilioAuthenticator = new MockTwilioAuthenticator(true);
        application.setMockTwilioAuthenticator(mockTwilioAuthenticator);

        Intent appsActivityIntent = new Intent(getTargetContext(), AppsActivity.class);
        appsActivityTestRule.launchActivity(appsActivityIntent);
    }

    @Test
    public void testEmptyTokensView() throws Exception {
        //Empty app list
        appsActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mockTwilioAuthenticator.setApps(new ArrayList<App>());
            }
        });

        // Check that the correct title is used
        CharSequence activityTitle = getTargetContext().getString(R.string.title_activity_apps_list);
        onView(withId(R.id.toolbar)).check(matches(new ToolbarTitleMatcher(is(activityTitle))));

        // Check that an empty token list is displayed
        onView(withId(R.id.tokens_list)).check(matches(isDisplayed()));
        onView(withId(R.id.tokens_list)).check(new RecyclerViewItemCountAssertion(0));
    }

    @Test
    public void testTokensViewWithTwoTokens() throws Exception {

        MockApp app1 = new MockApp(1L, "First App");
        MockApp app2 = new MockApp(2L, "Second App");

        //Empty app list
        final List<App> apps = new ArrayList<>();
        apps.add(app1);
        apps.add(app2);
        appsActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mockTwilioAuthenticator.setApps(apps);
            }
        });

        // Check that the correct title is used
        CharSequence activityTitle = getTargetContext().getString(R.string.title_activity_apps_list);
        onView(withId(R.id.toolbar)).check(matches(new ToolbarTitleMatcher(is(activityTitle))));

        // Check that a list with 2 token is displayed
        onView(withId(R.id.tokens_list)).check(matches(isDisplayed()));
        onView(withId(R.id.tokens_list)).check(new RecyclerViewItemCountAssertion(2));
    }
}