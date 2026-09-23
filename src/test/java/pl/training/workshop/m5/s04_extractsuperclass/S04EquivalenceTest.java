package pl.training.workshop.m5.s04_extractsuperclass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: lista konfliktów w salach jest identyczna w start i każdym kroku. */
final class S04EquivalenceTest {
    /** Rezerwacja w teście: seans (minutes > 0) albo standardowy wynajem (minutes == 0). */
    record Booking(String name, String hall, String start, int minutes) {
        boolean rental() {
            return minutes == 0;
        }

        LocalDateTime at() {
            return LocalDateTime.parse(start);
        }
    }

    @TestFactory
    Stream<DynamicTest> everyStepFindsTheSameConflicts() {
        return Scene.<List<Booking>, String>variants()
                .variant("start", S04EquivalenceTest::start)
                .variant("step1", S04EquivalenceTest::step1)
                .variant("step2", S04EquivalenceTest::step2)
                .variant("step3", S04EquivalenceTest::step3)
                .expect("seans nachodzi na wynajem", List.of(
                        new Booking("Diuna", "Sala 1", "2026-10-02T18:00", 166),
                        new Booking("Firma X", "Sala 1", "2026-10-02T20:00", 0)),
                        "Diuna x Wynajem: Firma X")
                .expect("różne sale - brak konfliktu", List.of(
                        new Booking("Diuna", "Sala 1", "2026-10-02T18:00", 166),
                        new Booking("Firma X", "Sala 2", "2026-10-02T18:00", 0)),
                        "")
                .expect("dwa seanse i dwa wynajmy", List.of(
                        new Booking("Kraina Lodu", "Sala 2", "2026-10-02T18:30", 102),
                        new Booking("Amator", "Sala 2", "2026-10-02T17:00", 100),
                        new Booking("Firma Y", "Sala 3", "2026-10-02T11:00", 0),
                        new Booking("Firma X", "Sala 3", "2026-10-02T10:00", 0)),
                        "Kraina Lodu x Amator; Wynajem: Firma Y x Wynajem: Firma X")
                .expect("koniec o 20:00 i start o 20:00 - styk to nie konflikt", List.of(
                        new Booking("Amator", "Sala 1", "2026-10-02T18:00", 120),
                        new Booking("Firma X", "Sala 1", "2026-10-02T20:00", 0)),
                        "")
                .tests();
    }

    private static String start(List<Booking> bookings) {
        var screenings = new ArrayList<pl.training.workshop.m5.s04_extractsuperclass.start.Screening>();
        var events = new ArrayList<pl.training.workshop.m5.s04_extractsuperclass.start.PrivateEvent>();
        for (Booking b : bookings) {
            if (b.rental()) {
                events.add(pl.training.workshop.m5.s04_extractsuperclass.start.PrivateEvent.rental(b.name(), b.hall(), b.at()));
            } else {
                screenings.add(new pl.training.workshop.m5.s04_extractsuperclass.start.Screening(b.name(), b.hall(), b.at(), b.minutes()));
            }
        }
        return String.join("; ", new pl.training.workshop.m5.s04_extractsuperclass.start.HallPlanner().conflicts(screenings, events));
    }

    private static String step1(List<Booking> bookings) {
        var screenings = new ArrayList<pl.training.workshop.m5.s04_extractsuperclass.step1.Screening>();
        var events = new ArrayList<pl.training.workshop.m5.s04_extractsuperclass.step1.PrivateEvent>();
        for (Booking b : bookings) {
            if (b.rental()) {
                events.add(pl.training.workshop.m5.s04_extractsuperclass.step1.PrivateEvent.rental(b.name(), b.hall(), b.at()));
            } else {
                screenings.add(new pl.training.workshop.m5.s04_extractsuperclass.step1.Screening(b.name(), b.hall(), b.at(), b.minutes()));
            }
        }
        return String.join("; ", new pl.training.workshop.m5.s04_extractsuperclass.step1.HallPlanner().conflicts(screenings, events));
    }

    private static String step2(List<Booking> bookings) {
        var screenings = new ArrayList<pl.training.workshop.m5.s04_extractsuperclass.step2.Screening>();
        var events = new ArrayList<pl.training.workshop.m5.s04_extractsuperclass.step2.PrivateEvent>();
        for (Booking b : bookings) {
            if (b.rental()) {
                events.add(pl.training.workshop.m5.s04_extractsuperclass.step2.PrivateEvent.rental(b.name(), b.hall(), b.at()));
            } else {
                screenings.add(new pl.training.workshop.m5.s04_extractsuperclass.step2.Screening(b.name(), b.hall(), b.at(), b.minutes()));
            }
        }
        return String.join("; ", new pl.training.workshop.m5.s04_extractsuperclass.step2.HallPlanner().conflicts(screenings, events));
    }

    private static String step3(List<Booking> bookings) {
        var screenings = new ArrayList<pl.training.workshop.m5.s04_extractsuperclass.step3.Screening>();
        var events = new ArrayList<pl.training.workshop.m5.s04_extractsuperclass.step3.PrivateEvent>();
        for (Booking b : bookings) {
            if (b.rental()) {
                events.add(pl.training.workshop.m5.s04_extractsuperclass.step3.PrivateEvent.rental(b.name(), b.hall(), b.at()));
            } else {
                screenings.add(new pl.training.workshop.m5.s04_extractsuperclass.step3.Screening(b.name(), b.hall(), b.at(), b.minutes()));
            }
        }
        return String.join("; ", new pl.training.workshop.m5.s04_extractsuperclass.step3.HallPlanner().conflicts(screenings, events));
    }
}
