package pl.training.workshop.m8.s02_stranglerfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

/** Routing fasady krok po kroku i dowód, że stary system zniknął. */
final class S02SolutionTest {
    @Test
    void facadeRoutesMoreOperationsToNewCodeWithEveryStep() {
        BookingLedger ledger = new BookingLedger();
        assertEquals(Map.of("book", "legacy", "report", "legacy"),
                new pl.training.workshop.m8.s02_stranglerfig.step1.CinemaFacade(ledger).routes());
        assertEquals(Map.of("book", "new", "report", "legacy"),
                new pl.training.workshop.m8.s02_stranglerfig.step2.CinemaFacade(ledger).routes());
        assertEquals(Map.of("book", "new", "report", "new"),
                new pl.training.workshop.m8.s02_stranglerfig.step3.CinemaFacade(ledger).routes());
    }

    @Test
    void legacyReportSeesBookingsTakenByTheNewModule() {
        BookingLedger ledger = new BookingLedger();
        var facade = new pl.training.workshop.m8.s02_stranglerfig.step2.CinemaFacade(ledger);
        facade.book("anna@kino.pl", "Amator", 1, 1, false);
        assertEquals("""
                RAPORT
                Amator: 1 bil., 25.00
                Biletow: 1
                Przychod z biletow: 25.00
                Oplaty rezerwacyjne: 0.00
                """, facade.report());
    }

    @Test
    void legacyCinemaIsDeletedInTheLastStep() {
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("pl.training.workshop.m8.s02_stranglerfig.step4.LegacyCinema"));
    }
}
