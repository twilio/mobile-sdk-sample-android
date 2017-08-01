package com.twilio.authsample.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Custom view to display a timer for a token
 * Created by jsuarez on 7/31/17.
 */

public class AuthyTimerView extends AppCompatTextView {
    private Paint arcPaint;
    private Paint backgroundPaint;
    private Paint dotPaint;

    private Rect arcRect;
    private RectF arcRectF;

    private int currentTime;
    private int totalTime;
    private int arcWidthDp;
    private int dotCenterX;
    private int dotCenterY;
    private int dotRadius;

    private int radius;
    private int angle;

    public AuthyTimerView(Context context) {
        super(context);
        init();
    }

    public AuthyTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AuthyTimerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        arcPaint = new Paint();
        backgroundPaint = new Paint();
        dotPaint = new Paint();

        arcRect = new Rect();
        arcRectF = new RectF();

        arcPaint.setAntiAlias(true);
        backgroundPaint.setAntiAlias(true);
        dotPaint.setAntiAlias(true);

        arcPaint.setColor(Color.GRAY);
        backgroundPaint.setColor(Color.WHITE);
        dotPaint.setColor(Color.RED);

        arcPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        backgroundPaint.setStyle(Paint.Style.FILL);
        dotPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        arcWidthDp = (int) getPixelsFromDp(getResources().getDisplayMetrics(), 5);
        dotRadius = (int) (arcWidthDp * 0.6);

        setWillNotDraw(false);
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
        invalidate();
    }

    /**
     * Set total time in seconds
     *
     * @param totalTime
     */
    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
        invalidate();
    }

    public void setTimerBackgroundColor(int color) {
        backgroundPaint.setColor(color);
    }

    public void setArcColor(int color) {
        this.arcPaint.setColor(color);
    }

    public void setDotColor(int color) {
        this.dotPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.getClipBounds(arcRect);
        arcRectF.bottom = arcRect.bottom;
        arcRectF.top = arcRect.top;
        arcRectF.left = arcRect.left;
        arcRectF.right = arcRect.right;

        radius = Math.max(canvas.getWidth(), canvas.getHeight()) / 2;
        angle = -getAngle(currentTime, totalTime);
        double angleRadians = Math.toRadians(180 - angle);
        dotCenterX = (int) (Math.sin(angleRadians) * (radius - dotRadius * 0.8)) + arcRect.width() / 2;
        dotCenterY = (int) (Math.cos(angleRadians) * (radius - dotRadius * 0.8)) + arcRect.height() / 2;
        // draw the arc that shows the remaining time
        canvas.drawArc(arcRectF, 270, angle, true, arcPaint);
        // draw the background to make the arc look like a ring
        canvas.drawCircle(arcRect.centerX(), arcRect.centerY(), radius - arcWidthDp, backgroundPaint);

        // draw the dot progress indicator
        canvas.drawCircle(dotCenterX, dotCenterY, dotRadius, dotPaint);

        super.onDraw(canvas);
    }

    private float getPixelsFromDp(DisplayMetrics displayMetrics, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    /**
     * Calculates the angle that represents the percentage of completion between currentTime and totalTime
     *
     * @param currentTime the current time (this value should always be <= totalTime)
     * @param totalTime   the total number of seconds
     */
    private static int getAngle(float currentTime, float totalTime) {
        float ratio = currentTime / totalTime;
        return (int) (360 * ratio);
    }
}
