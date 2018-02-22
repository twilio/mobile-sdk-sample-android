package com.twilio.authenticatorsample.appdetail;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * A Timer that posts messages to a handler every {@link #tickIntervalInMillis} milliseconds. You
 * can subscribe to be notified on every tick using the
 * {@link #setOnTickListener(com.twilio.authenticatorsample.appdetail.TimerTicker.OnTickListener)} method
 */
public class TimerTicker {

    private static final int TICK_WHAT = 2;
    private final long tickIntervalInMillis;
    private OnTickListener onTickListener;
    private Handler handler;
    private boolean started;
    private boolean running;

    public TimerTicker(final long tickIntervalInMillis) {
        this.tickIntervalInMillis = tickIntervalInMillis;
        this.handler = new TickerHandler(this);
    }


    private boolean isRunning() {
        return running;
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
        boolean running = started;
        if (running != this.running) {
            if (running) {
                sendTickAndPrepareNext();
            } else {
                handler.removeMessages(TICK_WHAT);
            }
            this.running = running;
        }
    }

    private void sendTickAndPrepareNext() {
        dispatchChronometerTick();
        handler.sendMessageDelayed(Message.obtain(handler, TICK_WHAT), tickIntervalInMillis);
    }

    /**
     * Starts the timer
     */
    public void start() {
        started = true;
        updateRunning();
    }

    /**
     * Stops the timer
     */
    public void stop() {
        started = false;
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
     * A listener to be notified on every tick
     */
    public interface OnTickListener {
        void onTick(TimerTicker timerTicker);
    }

    static class TickerHandler extends Handler {
        private WeakReference<TimerTicker> timerTicker;

        TickerHandler(TimerTicker timerTicker) {
            this.timerTicker = new WeakReference<>(timerTicker);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (timerTicker.get() != null && timerTicker.get().isRunning()) {
                timerTicker.get().sendTickAndPrepareNext();
            }
        }
    }
}
