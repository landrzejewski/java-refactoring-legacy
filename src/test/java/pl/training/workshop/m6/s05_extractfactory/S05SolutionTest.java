package pl.training.workshop.m6.s05_extractfactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m6.s05_extractfactory.step3.ReservationFactory;
import pl.training.workshop.m6.s05_extractfactory.step3.ReservationService;

/** Fabryka jest testowalna sama, a serwis dostaje ją jako zwykłą zależność. */
final class S05SolutionTest {
    private final Clock morning = Clock.fixed(Instant.parse("2026-10-03T09:50:00Z"), ZoneOffset.UTC);

    @Test
    void factoryAloneKnowsFeesAndExpiry() {
        Reservation reservation = new ReservationFactory(morning).create("ONLINE", "anna@kino.pl", List.of("A1", "A2"));
        assertEquals("R1", reservation.id());
        assertEquals("4.00", reservation.fee().toString());
        assertEquals(LocalDateTime.of(2026, 10, 3, 10, 5), reservation.expiresAt());
    }

    @Test
    void twoServicesSharingOneFactoryShareNumbering() {
        ReservationFactory factory = new ReservationFactory(morning);
        new ReservationService(factory).reserve("BOX_OFFICE", "jan@kino.pl", List.of("A1"));
        Reservation second = new ReservationService(factory).reserve("BOX_OFFICE", "jan@kino.pl", List.of("A1"));
        assertEquals("R2", second.id());
    }
}
