package com.twilio.authenticatorsample.matchers;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Created by jsuarez on 6/1/16.
 */
public class ToolbarTitleMatcher extends BoundedMatcher<Object, Toolbar> {
    private final Matcher<CharSequence> textMatcher;

    public ToolbarTitleMatcher(Matcher<CharSequence> textMatcher) {
        super(Toolbar.class);
        this.textMatcher = textMatcher;
    }

    @Override
    protected boolean matchesSafely(Toolbar toolbar) {
        return textMatcher.matches(toolbar.getTitle());
    }

    @Override
    public void describeTo(Description description) {

    }
}
