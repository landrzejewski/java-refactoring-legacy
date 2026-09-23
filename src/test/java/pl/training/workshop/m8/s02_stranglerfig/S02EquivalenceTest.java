package pl.training.workshop.m8.s02_stranglerfig;

import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Test równoważności: ten sam scenariusz klienta (rezerwacje + raport) daje ten sam zapis
 * niezależnie od tego, czy operacje obsługuje legacy, nowy moduł czy mieszanka obu.
 */
final class S02EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> clientsSeeTheSameSystemAfterEveryStep() {
        return Scene.<Boolean, String>variants()
                .variant("start", withBookings -> run(
                        pl.training.workshop.m8.s02_stranglerfig.start.LegacyCinema::new, withBookings))
                .variant("step1", withBookings -> run(
                        pl.training.workshop.m8.s02_stranglerfig.step1.CinemaFacade::new, withBookings))
                .variant("step2", withBookings -> run(
                        pl.training.workshop.m8.s02_stranglerfig.step2.CinemaFacade::new, withBookings))
                .variant("step3", withBookings -> run(
                        pl.training.workshop.m8.s02_stranglerfig.step3.CinemaFacade::new, withBookings))
                .variant("step4", withBookings -> run(
                        pl.training.workshop.m8.s02_stranglerfig.step4.CinemaFacade::new, withBookings))
                .expect("rezerwacje (w tym błędna) i raport", true, """
                        B1
                        B2
                        ERROR: no seats
                        B3
                        RAPORT
                        Amator: 10 bil., 225.00
                        Diuna: 2 bil., 80.00
                        Kraina Lodu: 1 bil., 32.00
                        Biletow: 13
                        Przychod z biletow: 337.00
                        Oplaty rezerwacyjne: 6.00
                        """)
                .expect("raport pustej bazy", false, """
                        RAPORT
                        Biletow: 0
                        Przychod z biletow: 0.00
                        Oplaty rezerwacyjne: 0.00
                        """)
                .tests();
    }

    private static String run(Function<BookingLedger, CinemaApi> system, boolean withBookings) {
        CinemaApi api = system.apply(new BookingLedger());
        if (!withBookings) {
            return api.report();
        }
        return api.book("anna@kino.pl", "Diuna", 3, 2, true) + "\n"
                + api.book("jan@kino.pl", "Amator", 1, 10, false) + "\n"
                + api.book("ola@kino.pl", "Kraina Lodu", 2, 0, true) + "\n"
                + api.book("ola@kino.pl", "Kraina Lodu", 2, 1, true) + "\n"
                + api.report();
    }
}
