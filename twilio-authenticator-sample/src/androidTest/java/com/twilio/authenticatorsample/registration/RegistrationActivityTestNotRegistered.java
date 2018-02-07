package com.twilio.authenticatorsample.registration;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.twilio.authenticatorsample.R;
import com.twilio.authenticatorsample.mocks.MockTwilioAuthenticator;
import com.twilio.authenticatorsample.mocks.TestSampleApp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by jsuarez on 5/12/16.
 */
@RunWith(AndroidJUnit4.class)
public class RegistrationActivityTestNotRegistered {

    @Rule
    public IntentsTestRule<RegistrationActivity> registrationActivityTestRule =
            new IntentsTestRule<>(RegistrationActivity.class, false, false);

    @Before
    public void setUp() throws Exception {
        TestSampleApp application = TestSampleApp.TEST_APP;
        application.setMockTwilioAuthenticator(new MockTwilioAuthenticator(false));
        registrationActivityTestRule.launchActivity(new Intent(getTargetContext(), RegistrationActivity.class));
    }

    @Test
    public void testRegistrationFormIsDisplayed() throws Exception {

        // Check that all required views are visible
        onView(withId(R.id.form)).perform(closeSoftKeyboard());
        onView(withId(R.id.user_id)).check(matches(isDisplayed()));
        onView(withId(R.id.backend_url)).check(matches(isDisplayed()));
        onView(withId(R.id.registerDeviceButton)).check(matches(isDisplayed()));
        onView(withId(R.id.signupTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterDeviceButton() throws Exception {
        // Test wrong authy id input
        onView(withId(R.id.user_id)).perform(replaceText(""));
        onView(withId(R.id.user_id)).perform(closeSoftKeyboard());
        onView(withId(R.id.registerDeviceButton)).perform(click());
        onView(withId(R.id.user_id)).check(matches(hasErrorText(registrationActivityTestRule.getActivity().getString(R.string.registration_error_invalid_field))));

        // Test valid authy id and wrong url
        onView(withId(R.id.user_id)).perform(replaceText("123456"));
        onView(withId(R.id.backend_url)).perform(replaceText("invalid url"));
        onView(withId(R.id.backend_url)).perform(closeSoftKeyboard());
        onView(withId(R.id.registerDeviceButton)).perform(click());
        onView(withId(R.id.backend_url)).check(matches(hasErrorText(registrationActivityTestRule.getActivity().getString(R.string.registration_error_invalid_field))));

    }
}