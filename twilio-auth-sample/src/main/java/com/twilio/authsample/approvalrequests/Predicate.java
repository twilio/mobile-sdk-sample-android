package com.twilio.authsample.approvalrequests;

/**
 * Small interface to help filtering items of type T
 * Created by jsuarez on 4/21/16.
 */
public interface Predicate<T> {
    boolean apply(T type);
}
