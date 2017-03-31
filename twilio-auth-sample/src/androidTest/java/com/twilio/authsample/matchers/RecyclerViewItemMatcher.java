package com.twilio.authsample.matchers;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;

/**
 * Created by jsuarez on 6/1/16.
 */
public class RecyclerViewItemMatcher extends BoundedMatcher<View, RecyclerView> {
    private final int position;
    @NonNull
    private final Matcher<View> itemMatcher;

    public RecyclerViewItemMatcher(int position, @NonNull final Matcher<View> itemMatcher) {
        super(RecyclerView.class);
        this.position = position;
        this.itemMatcher = itemMatcher;
        checkNotNull(itemMatcher);
    }

    @Override
    protected boolean matchesSafely(RecyclerView view) {
        RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
        if (viewHolder == null) {
            // has no item on such position
            return false;
        }
        return itemMatcher.matches(viewHolder.itemView);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("has item at position " + position + ": ");
        itemMatcher.describeTo(description);
    }
}
