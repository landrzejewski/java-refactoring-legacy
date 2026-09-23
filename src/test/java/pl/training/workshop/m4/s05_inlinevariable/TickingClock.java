package pl.training.workshop.m4.s05_inlinevariable;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/** Ręczny fake zegara: każdy odczyt przesuwa czas o sekundę - jak prawdziwy zegar, tylko przewidywalnie. */
final class TickingClock extends Clock {
    private Instant now;

    TickingClock(Instant start) {
        this.now = start;
    }

    @Override
    public Instant instant() {
        Instant current = now;
        now = now.plus(Duration.ofSeconds(1));
        return current;
    }

    @Override
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }
}
