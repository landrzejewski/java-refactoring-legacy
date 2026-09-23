package pl.training.workshop.m8.s05_boyscout;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Test równoważności: start i poprawna poprawa (step2). Krok 1 celowo NIE jest tu wariantem -
 * to nadużycie, które S05SolutionTest demaskuje.
 */
final class S05EquivalenceTest {
    static final Ticket REGULAR = new Ticket("Amator", LocalDateTime.of(2026, 3, 14, 18, 0),
            List.of("C5"), "jan@kino.pl", "600100200", 25.00);
    static final Ticket ROWS_9_AND_10 = new Ticket("Diuna", LocalDateTime.of(2026, 3, 13, 20, 0),
            List.of("A9", "A10"), "anna@kino.pl", "600100300", 84.00);
    static final Ticket NO_PHONE = new Ticket("Kraina Lodu", LocalDateTime.of(2026, 3, 14, 10, 30),
            List.of("B1", "B2"), "ola@kino.pl", null, 47.20);
    static final Ticket MIXED_CASE_EMAIL = new Ticket("Amator", LocalDateTime.of(2026, 3, 14, 18, 0),
            List.of("D7"), " Anna@Kino.pl ", "600100400", 25.00);

    @TestFactory
    Stream<DynamicTest> boyScoutCleanupKeepsTheTicketIdentical() {
        return Scene.<Ticket, String>variants()
                .variant("start", new pl.training.workshop.m8.s05_boyscout.start.TicketPrinter()::print)
                .variant("step2", new pl.training.workshop.m8.s05_boyscout.step2.TicketPrinter()::print)
                .expect("zwykły bilet", REGULAR, """
                        Film: Amator
                        Seans: 2026-03-14 18:00
                        Miejsca: C5
                        Klient: jan@kino.pl
                        Tel: 600100200
                        Do zaplaty: 25.00
                        """)
                .expect("miejsca w rzędach 9 i 10", ROWS_9_AND_10, """
                        Film: Diuna
                        Seans: 2026-03-13 20:00
                        Miejsca: A9, A10
                        Klient: anna@kino.pl
                        Tel: 600100300
                        Do zaplaty: 84.00
                        """)
                .expect("brak telefonu", NO_PHONE, """
                        Film: Kraina Lodu
                        Seans: 2026-03-14 10:30
                        Miejsca: B1, B2
                        Klient: ola@kino.pl
                        Tel: -
                        Do zaplaty: 47.20
                        """)
                .expect("e-mail z wielkimi literami", MIXED_CASE_EMAIL, """
                        Film: Amator
                        Seans: 2026-03-14 18:00
                        Miejsca: D7
                        Klient: Anna@Kino.pl
                        Tel: 600100400
                        Do zaplaty: 25.00
                        """)
                .tests();
    }
}
