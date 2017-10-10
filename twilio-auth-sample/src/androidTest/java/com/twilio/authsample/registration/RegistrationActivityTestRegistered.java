package com.twilio.authsample.registration;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.core.Is.is;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.twilio.authsample.R;
import com.twilio.authsample.matchers.ToolbarTitleMatcher;
import com.twilio.authsample.mocks.MockTwilioAuthenticator;
import com.twilio.authsample.mocks.TestApp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by jsuarez on 5/12/16.
 */
@RunWith(AndroidJUnit4.class)
public class RegistrationActivityTestRegistered {

    @Rule
    public IntentsTestRule<RegistrationActivity> registrationActivityTestRule =
            new IntentsTestRule<>(RegistrationActivity.class, false, false);


    @Before
    public void setUp() throws Exception {
        TestApp application = TestApp.TEST_APP;
        application.setMockTwilioAuthenticator(new MockTwilioAuthenticator(application, true));
        registrationActivityTestRule.launchActivity(new Intent(getTargetContext(), RegistrationActivity.class));
    }

    @Test
    public void testRegistrationFormIsNotDisplayed() throws Exception {
        // Form views should not be visible
        onView(withId(R.id.authy_id)).check(doesNotExist());
        onView(withId(R.id.backend_url)).check(doesNotExist());
        onView(withId(R.id.registerDeviceButton)).check(doesNotExist());
        onView(withId(R.id.signupTitle)).check(doesNotExist());
    }

    @Test
    public void testApprovalRequestListActivityTransition() throws Exception {
        // Check that the MainActivity is launched
        CharSequence mainActivityTitle = getTargetContext().getString(R.string.menu_navigation_requests);
        onView(withId(R.id.toolbar)).check(matches(new ToolbarTitleMatcher(is(mainActivityTitle))));

    }
}