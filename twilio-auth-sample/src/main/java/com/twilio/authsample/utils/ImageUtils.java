package com.twilio.authsample.utils;

import com.twilio.authenticator.external.ApprovalRequestLogo;

import java.util.List;

/**
 * Created by jsuarez on 3/16/16.
 */
public class ImageUtils {
    public static String getMostSuitableImageUrl(List<? extends ApprovalRequestLogo> images) {
        if (images.isEmpty()) {
            return null;
        }

        for (ApprovalRequestLogo approvalRequestLogo : images) {
            if (approvalRequestLogo.getResolution().equals("default")) {
                return approvalRequestLogo.getUrl();
            }
        }
        return images.get(0).getUrl();
    }
}
