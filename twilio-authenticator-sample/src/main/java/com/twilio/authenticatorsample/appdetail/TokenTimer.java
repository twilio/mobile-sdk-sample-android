package com.twilio.authenticatorsample.appdetail;

/**
 * A timer utility class that takes a token as input and notifies listeners when the timer is ticking
 * and when the timer is done so the user can reset the timer and/or calculate new otps.
 */
public class TokenTimer implements TimerTicker.OnTickListener {
    private final TimerTicker ticker;
    private final long tickIntervalTimeMillis;
    private final long timerLengthMillis;
    private long remainingMillis;
    private OnTimerListener onTimerListener;

    /**
     * Initializes a new token timer
     *
     * @param tickIntervalTimeMillis the interval between tick events. This value is used to determine how
     *                               often the {@link OnTimerListener listener} should be called.
     * @param timerLengthMillis      the complete length of the timer in milliseconds
     */
    public TokenTimer(long tickIntervalTimeMillis, long timerLengthMillis) {
        this.ticker = new TimerTicker(tickIntervalTimeMillis);
        this.tickIntervalTimeMillis = tickIntervalTimeMillis;
        this.timerLengthMillis = timerLengthMillis;
        this.ticker.setOnTickListener(this);
    }

    /**
     * Starts the timer for a given token.
     */
    public void start() {
        remainingMillis = initTimer();
        ticker.start();
    }

    /**
     * Stops the timer. No moire tick events will be fired.
     */
    public void stop() {
        ticker.stop();
    }

    /**
     * @return calculates and returns the size of the timer in millis.
     */
    private long initTimer() {
        return timerLengthMillis;
    }

    /**
     * Sets a listener for the timer's tick events
     *
     * @param onTimerListener the listener
     */
    public void setOnTimerListener(OnTimerListener onTimerListener) {
        this.onTimerListener = onTimerListener;
    }

    @Override
    public void onTick(TimerTicker timerTicker) {
        // This method is called every time the timer ticks.
        // if the timer reaches 0 (or less) then a timer elapsed event will be notified
        // to the listener.
        if (remainingMillis <= 0) {
            dispatchTimerElapsed();
            remainingMillis = initTimer();
        }
        dispatchTimerTick();
        remainingMillis -= tickIntervalTimeMillis;
    }

    void dispatchTimerElapsed() {
        if (onTimerListener != null) {
            onTimerListener.onTokenTimerElapsed(this);
        }
    }

    void dispatchTimerTick() {
        if (onTimerListener != null) {
            onTimerListener.onTimerTick(this);
        }
    }

    /**
     * Restarting the timer is equivalent to calling stop and then start
     */
    public void restart() {
        stop();
        start();
    }

    public long getRemainingSeconds() {
        return remainingMillis / 1000;
    }

    public long getRemainingMillis() {
        return remainingMillis;
    }

    /**
     * A listener interface that is notified of updates in the timer
     */
    public interface OnTimerListener {

        /**
         * This method is called when the timer reaches 0 (i.e. when it finishes)
         *
         * @param tokenTimer the timer that is ticking.
         */
        public void onTokenTimerElapsed(TokenTimer tokenTimer);

        /**
         * This method is called every time the timer ticks. You can use {@link #getRemainingMillis()}
         * or {@link #getRemainingSeconds()} to find out how much time is left in the timer.
         *
         * @param tokenTimer the timer that is ticking.
         */
        public void onTimerTick(TokenTimer tokenTimer);
    }
}
