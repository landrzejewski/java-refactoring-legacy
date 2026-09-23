package pl.training.workshop.m7.s07_arrowhead;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Macierz gałęzi: każda ścieżka plus kombinacje sprawdzające priorytet warunków.
 * Obserwujemy wynik ORAZ audyt - guard clause wstawiona w book() zgubiłaby wpis.
 */
final class S07EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepKeepsBranchesPriorityAndAudit() {
        return Scene.<BookingAttempt, String>variants()
                .variant("start", observe(pl.training.workshop.m7.s07_arrowhead.start.BookingGate::new,
                        pl.training.workshop.m7.s07_arrowhead.start.BookingGate::book,
                        pl.training.workshop.m7.s07_arrowhead.start.BookingGate::audit))
                .variant("step1", observe(pl.training.workshop.m7.s07_arrowhead.step1.BookingGate::new,
                        pl.training.workshop.m7.s07_arrowhead.step1.BookingGate::book,
                        pl.training.workshop.m7.s07_arrowhead.step1.BookingGate::audit))
                .variant("step2", observe(pl.training.workshop.m7.s07_arrowhead.step2.BookingGate::new,
                        pl.training.workshop.m7.s07_arrowhead.step2.BookingGate::book,
                        pl.training.workshop.m7.s07_arrowhead.step2.BookingGate::audit))
                .variant("step3", observe(pl.training.workshop.m7.s07_arrowhead.step3.BookingGate::new,
                        pl.training.workshop.m7.s07_arrowhead.step3.BookingGate::book,
                        pl.training.workshop.m7.s07_arrowhead.step3.BookingGate::audit))
                .expect("sciezka glowna", attempt(true, true, false, 2, 5), "BOOKED [anna@kino.pl -> BOOKED]")
                .expect("dokladnie tyle wolnych", attempt(true, true, false, 5, 5), "BOOKED [anna@kino.pl -> BOOKED]")
                .expect("brak seansu wygrywa ze wszystkim", attempt(false, false, true, 0, 0),
                        "NO_SCREENING [anna@kino.pl -> NO_SCREENING]")
                .expect("sprzedaz zamknieta przed blokada klienta", attempt(true, false, true, 2, 5),
                        "SALES_CLOSED [anna@kino.pl -> SALES_CLOSED]")
                .expect("blokada klienta przed liczba miejsc", attempt(true, true, true, 0, 5),
                        "CUSTOMER_BLOCKED [anna@kino.pl -> CUSTOMER_BLOCKED]")
                .expect("zero miejsc przed brakiem wolnych", attempt(true, true, false, 0, 0),
                        "NO_SEATS_REQUESTED [anna@kino.pl -> NO_SEATS_REQUESTED]")
                .expect("wyprzedane", attempt(true, true, false, 6, 5), "SOLD_OUT [anna@kino.pl -> SOLD_OUT]")
                .tests();
    }

    private static BookingAttempt attempt(boolean found, boolean open, boolean blocked, int requested, int free) {
        return new BookingAttempt("anna@kino.pl", found, open, blocked, requested, free);
    }

    private static <G> Function<BookingAttempt, String> observe(Supplier<G> gates,
            BiFunction<G, BookingAttempt, String> book, Function<G, List<String>> audit) {
        return attempt -> {
            G gate = gates.get();
            String result = book.apply(gate, attempt);
            return result + " " + audit.apply(gate);
        };
    }
}
