package pl.training.workshop.m3.s15_invariants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Ścieżka przez serwis działa identycznie we wszystkich wariantach (także komunikaty błędów).
 * Ścieżka importu: do kroku 1 przepuszcza złe dane (pułapka), w kroku 2 je odrzuca.
 */
final class S15InvariantsTest {
    record Request(String email, int seats, String total) {
    }

    private static String outcome(Supplier<String> action) {
        try {
            return action.get();
        } catch (IllegalArgumentException e) {
            return "blad: " + e.getMessage();
        }
    }

    @TestFactory
    Stream<DynamicTest> bookingServiceBehavesTheSame() {
        return Scene.<Request, String>variants()
                .variant("start", r -> outcome(() -> new pl.training.workshop.m3.s15_invariants.start.BookingService()
                        .book(r.email(), r.seats(), new BigDecimal(r.total()))))
                .variant("step1", r -> outcome(() -> new pl.training.workshop.m3.s15_invariants.step1.BookingService()
                        .book(r.email(), r.seats(), new BigDecimal(r.total()))))
                .variant("step2", r -> outcome(() -> new pl.training.workshop.m3.s15_invariants.step2.BookingService()
                        .book(r.email(), r.seats(), new BigDecimal(r.total()))))
                .expect("poprawna rezerwacja", new Request("anna@kino.pl", 2, "50.00"),
                        "zarezerwowano: anna@kino.pl, miejsc 2, kwota 50.00")
                .expect("zly email", new Request("jan-kino.pl", 1, "25.00"), "blad: niepoprawny email: jan-kino.pl")
                .expect("zero miejsc", new Request("jan@kino.pl", 0, "0.00"),
                        "blad: liczba miejsc musi byc dodatnia: 0")
                .expect("ujemna kwota", new Request("jan@kino.pl", 1, "-5.00"), "blad: kwota nie moze byc ujemna: -5.00")
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> importOfValidLineBehavesTheSame() {
        return Scene.<String, String>variants()
                .variant("start", new pl.training.workshop.m3.s15_invariants.start.ReservationImport()::importLine)
                .variant("step1", new pl.training.workshop.m3.s15_invariants.step1.ReservationImport()::importLine)
                .variant("step2", new pl.training.workshop.m3.s15_invariants.step2.ReservationImport()::importLine)
                .expect("poprawny wiersz", "anna@kino.pl;2;50.00", "zaimportowano: anna@kino.pl, miejsc 2, kwota 50.00")
                .tests();
    }

    @Test
    void importWithoutInvariantsCreatesInvalidReservation() {
        // krok 1 = start pod tym względem (start jest edytowany na żywo, więc sprawdzamy kopię)
        assertEquals("zaimportowano: jan-kino.pl, miejsc 0, kwota -5.00",
                new pl.training.workshop.m3.s15_invariants.step1.ReservationImport().importLine("jan-kino.pl;0;-5.00"));
    }

    @Test
    void step2ImportCannotCreateInvalidReservation() {
        var importer = new pl.training.workshop.m3.s15_invariants.step2.ReservationImport();
        var error = assertThrows(IllegalArgumentException.class, () -> importer.importLine("jan-kino.pl;0;-5.00"));
        assertEquals("niepoprawny email: jan-kino.pl", error.getMessage());
    }
}
