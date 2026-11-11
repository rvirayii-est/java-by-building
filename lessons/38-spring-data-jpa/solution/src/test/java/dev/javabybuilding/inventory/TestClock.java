package dev.javabybuilding.inventory;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/**
 * A Clock for tests that you can move forward by hand. Clock.fixed always says the same instant; this one says
 * the same instant until you call advance. So a test can sell something "on Monday" and something else "on Tuesday"
 * in a few milliseconds, and the answer never depends on the real date.
 */
final class TestClock extends Clock {

    private Instant now;
    private final ZoneId zone;

    TestClock(Instant start, ZoneId zone) {
        this.now = start;
        this.zone = zone;
    }

    /** Moves the clock forward by the given amount of time. */
    void advance(Duration amount) {
        now = now.plus(amount);
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new TestClock(now, zone);
    }

    @Override
    public Instant instant() {
        return now;
    }
}
