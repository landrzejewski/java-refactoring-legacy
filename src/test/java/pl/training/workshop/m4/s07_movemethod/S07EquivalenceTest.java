package pl.training.workshop.m4.s07_movemethod;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: ten sam wydruk rezerwacji po każdym przeniesieniu metody. */
final class S07EquivalenceTest {
    static final BookingData DIUNA = new BookingData("R-1", "Diuna", 3, LocalDateTime.of(2026, 9, 25, 20, 0),
            1, List.of(1, 2, 3, 4, 5), 1);

    @TestFactory
    Stream<DynamicTest> everyStepPrintsTheSameBooking() {
        return Scene.<BookingData, String>variants()
                .variant("start", d -> new pl.training.workshop.m4.s07_movemethod.start.BookingPrinter().print(
                        new pl.training.workshop.m4.s07_movemethod.start.Booking(d.id(),
                                new pl.training.workshop.m4.s07_movemethod.start.Screening(
                                        d.title(), d.format(), d.start(), d.hall(), d.freeSeats()), d.seat())))
                .variant("step1", d -> new pl.training.workshop.m4.s07_movemethod.step1.BookingPrinter().print(
                        new pl.training.workshop.m4.s07_movemethod.step1.Booking(d.id(),
                                new pl.training.workshop.m4.s07_movemethod.step1.Screening(
                                        d.title(), d.format(), d.start(), d.hall(), d.freeSeats()), d.seat())))
                .variant("step2", d -> new pl.training.workshop.m4.s07_movemethod.step2.BookingPrinter().print(
                        new pl.training.workshop.m4.s07_movemethod.step2.Booking(d.id(),
                                new pl.training.workshop.m4.s07_movemethod.step2.Screening(
                                        d.title(), d.format(), d.start(), d.hall(), d.freeSeats()), d.seat())))
                .expect("IMAX, miejsce 1 (wartość 1 != indeks 1)", DIUNA, """
                        Rezerwacja R-1
                        Diuna (IMAX), sala 1, 2026-09-25 20:00
                        Miejsce: 1
                        Pozostale wolne: [2, 3, 4, 5]
                        """)
                .expect("3D rano, miejsce 8 (indeks 8 nie istnieje)",
                        new BookingData("R-2", "Kraina Lodu", 2, LocalDateTime.of(2026, 9, 26, 10, 30),
                                2, List.of(7, 8, 9), 8), """
                        Rezerwacja R-2
                        Kraina Lodu (3D), sala 2, 2026-09-26 10:30
                        Miejsce: 8
                        Pozostale wolne: [7, 9]
                        """)
                .expect("2D, miejsce 2 na pozycji 2 - ten przypadek NIE odróżni remove(int)",
                        new BookingData("R-3", "Amator", 1, LocalDateTime.of(2026, 9, 26, 18, 0),
                                3, List.of(3, 1, 2), 2), """
                        Rezerwacja R-3
                        Amator (2D), sala 3, 2026-09-26 18:00
                        Miejsce: 2
                        Pozostale wolne: [3, 1]
                        """)
                .tests();
    }
}
