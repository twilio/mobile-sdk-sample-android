package com.twilio.authsample.totp;

import android.os.Handler;
import android.os.Message;

/**
 * A Timer that posts messages to a handler every {@link #tickIntervalInMillis} milliseconds. You can subscribe to elapsed seconds via
 * the {@link #setOnTickListener(com.twilio.authsample.totp.TimerTicker.OnTickListener)} method
 */
public class TimerTicker {

    private final long tickIntervalInMillis;

    private OnTickListener onTickListener;

    private Handler mHandler;

    private static final int TICK_WHAT = 2;
    private boolean mStarted;
    private boolean mRunning;

    public TimerTicker(final long tickIntervalInMillis) {
        // FIXME probable memory leak
        this.tickIntervalInMillis = tickIntervalInMillis;
        this.mHandler = new Handler() {
            public void handleMessage(Message m) {
                if (mRunning) {
                    dispatchChronometerTick();
                    sendMessageDelayed(Message.obtain(this, TICK_WHAT), tickIntervalInMillis);
                }
            }
        };
    }

    /**
     * Sets the listener to be called when the chronometer changes.
     *
     * @param onTickListener The listener.
     */
    public void setOnTickListener(OnTickListener onTickListener) {
        this.onTickListener = onTickListener;
    }

    private void updateRunning() {
        boolean running = mStarted;
        if (running != mRunning) {
            if (running) {
                dispatchChronometerTick();
                mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_WHAT), tickIntervalInMillis);
            } else {
                mHandler.removeMessages(TICK_WHAT);
            }
            mRunning = running;
        }
    }

    /**
     * Starts the timer
     */
    public void start() {
        mStarted = true;
        updateRunning();
    }

    /**
     * Stops the timer
     */
    public void stop() {
        mStarted = false;
        updateRunning();
    }

    /**
     * If the listener is set, call the {@link OnTickListener#onTick(TimerTicker) onTick} method, if
     * no listener is set, do nothing.
     */
    public void dispatchChronometerTick() {
        if (onTickListener != null) {
            onTickListener.onTick(this);
        }
    }

    /**
     * A listener to be notified.
     */
    public interface OnTickListener {
        public void onTick(TimerTicker timerTicker);
    }
}
