package pl.training.workshop.m7.s01_breakdependencies;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Scena s01: start jest celowo nietestowalny. Każdy krok otwiera kolejny seam,
 * a test rośnie razem z nim: separacja bazy, kontrola czasu, obserwacja maili.
 */
final class S01SeamTest {
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 3, 10, 18, 0);

    @Test
    void startCannotEvenRunInATest() {
        var job = new pl.training.workshop.m7.s01_breakdependencies.start.ShowtimeReminderJob();
        IllegalStateException error = assertThrows(IllegalStateException.class, job::run);
        assertTrue(error.getMessage().startsWith("brak polaczenia"));
    }

    @Test
    void step1KeepsConnectionLifetimeOfProductionConstructor() {
        var job = assertDoesNotThrow(
                () -> new pl.training.workshop.m7.s01_breakdependencies.step1.ShowtimeReminderJob());
        assertThrows(IllegalStateException.class, job::run);
    }

    @Test
    void step1RunsWithFakeStoreWhenNothingIsDue() {
        FakeStore store = new FakeStore(
                booking("B1", LocalDateTime.of(2020, 1, 1, 20, 0), false),
                booking("B2", LocalDateTime.of(2026, 3, 10, 19, 0), true));
        var job = new pl.training.workshop.m7.s01_breakdependencies.step1.ShowtimeReminderJob(store::step1);
        assertEquals(0, job.run());
        assertEquals(List.of(), store.marked);
    }

    @Test
    void step2ControlsTimeButStillHitsStaticMailer() {
        Clock clock = Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        FakeStore notDue = new FakeStore(
                booking("B1", NOW.plusMinutes(121), false),
                booking("B2", NOW, false));
        var quiet = new pl.training.workshop.m7.s01_breakdependencies.step2.ShowtimeReminderJob(
                notDue::step2, clock);
        assertEquals(0, quiet.run());

        FakeStore due = new FakeStore(booking("B3", NOW.plusMinutes(120), false));
        var noisy = new pl.training.workshop.m7.s01_breakdependencies.step2.ShowtimeReminderJob(
                due::step2, clock);
        IllegalStateException error = assertThrows(IllegalStateException.class, noisy::run);
        assertTrue(error.getMessage().startsWith("SMTP"));
    }

    @TestFactory
    Stream<DynamicTest> firstRealTestForSteps3And4() {
        return Scene.<List<PaidBooking>, String>variants()
                .variant("step3", S01SeamTest::runStep3)
                .variant("step4", S01SeamTest::runStep4)
                .expect("seans za 90 minut",
                        List.of(booking("B1", NOW.plusMinutes(90), false)),
                        "sent=1 [MAIL anna@kino.pl | Przypomnienie: Diuna | Seans zaczyna sie o 19:30]"
                                + " marked=[B1]")
                .expect("granica 120 minut wchodzi, 121 nie",
                        List.of(booking("B1", NOW.plusMinutes(120), false),
                                booking("B2", NOW.plusMinutes(121), false)),
                        "sent=1 [MAIL anna@kino.pl | Przypomnienie: Diuna | Seans zaczyna sie o 20:00]"
                                + " marked=[B1]")
                .expect("juz przypomniane i juz rozpoczete",
                        List.of(booking("B1", NOW.plusMinutes(30), true),
                                booking("B2", NOW.minusMinutes(1), false)),
                        "sent=0 [] marked=[]")
                .tests();
    }

    private static String runStep3(List<PaidBooking> bookings) {
        FakeStore store = new FakeStore(bookings);
        List<String> mails = new ArrayList<>();
        var job = new pl.training.workshop.m7.s01_breakdependencies.step3.ShowtimeReminderJob(
                store::step3, fixedClock()) {
            @Override
            protected void sendReminder(String to, String subject, String body) {
                mails.add("MAIL " + to + " | " + subject + " | " + body);
            }
        };
        return "sent=" + job.run() + " " + mails + " marked=" + store.marked;
    }

    private static String runStep4(List<PaidBooking> bookings) {
        FakeStore store = new FakeStore(bookings);
        List<String> mails = new ArrayList<>();
        var job = new pl.training.workshop.m7.s01_breakdependencies.step4.ShowtimeReminderJob(
                store::step4, fixedClock(),
                (to, subject, body) -> mails.add("MAIL " + to + " | " + subject + " | " + body));
        return "sent=" + job.run() + " " + mails + " marked=" + store.marked;
    }

    private static Clock fixedClock() {
        return Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    }

    private static PaidBooking booking(String id, LocalDateTime start, boolean reminded) {
        return new PaidBooking(id, "anna@kino.pl", "Diuna", start, reminded);
    }

    /** Ręczny fake bazy: podaje rezerwacje i zapamiętuje oznaczone jako przypomniane. */
    private static final class FakeStore {
        private final List<PaidBooking> bookings;
        private final List<String> marked = new ArrayList<>();

        FakeStore(PaidBooking... bookings) {
            this(List.of(bookings));
        }

        FakeStore(List<PaidBooking> bookings) {
            this.bookings = bookings;
        }

        pl.training.workshop.m7.s01_breakdependencies.step1.BookingStore step1() {
            return new pl.training.workshop.m7.s01_breakdependencies.step1.BookingStore() {
                @Override
                public List<PaidBooking> paidBookings() {
                    return bookings;
                }

                @Override
                public void markReminded(String bookingId) {
                    marked.add(bookingId);
                }
            };
        }

        pl.training.workshop.m7.s01_breakdependencies.step2.BookingStore step2() {
            return new pl.training.workshop.m7.s01_breakdependencies.step2.BookingStore() {
                @Override
                public List<PaidBooking> paidBookings() {
                    return bookings;
                }

                @Override
                public void markReminded(String bookingId) {
                    marked.add(bookingId);
                }
            };
        }

        pl.training.workshop.m7.s01_breakdependencies.step3.BookingStore step3() {
            return new pl.training.workshop.m7.s01_breakdependencies.step3.BookingStore() {
                @Override
                public List<PaidBooking> paidBookings() {
                    return bookings;
                }

                @Override
                public void markReminded(String bookingId) {
                    marked.add(bookingId);
                }
            };
        }

        pl.training.workshop.m7.s01_breakdependencies.step4.BookingStore step4() {
            return new pl.training.workshop.m7.s01_breakdependencies.step4.BookingStore() {
                @Override
                public List<PaidBooking> paidBookings() {
                    return bookings;
                }

                @Override
                public void markReminded(String bookingId) {
                    marked.add(bookingId);
                }
            };
        }
    }
}
