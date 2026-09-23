package pl.training.workshop.m4.s09_extractclass;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: to samo podsumowanie i te same błędy płatności po każdym Extract Class. */
final class S09EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepSummarizesBookingsTheSameWay() {
        return Scene.<BookingInput, String>variants()
                .variant("start", in -> {
                    var booking = new pl.training.workshop.m4.s09_extractclass.start.Booking(
                            in.id(), in.name(), in.email(), in.phone(), in.amount());
                    return run(in, booking::pay, booking::summary);
                })
                .variant("step1", in -> {
                    var booking = new pl.training.workshop.m4.s09_extractclass.step1.Booking(
                            in.id(), in.name(), in.email(), in.phone(), in.amount());
                    return run(in, booking::pay, booking::summary);
                })
                .variant("step2", in -> {
                    var booking = new pl.training.workshop.m4.s09_extractclass.step2.Booking(
                            in.id(), in.name(), in.email(), in.phone(), in.amount());
                    return run(in, booking::pay, booking::summary);
                })
                .variant("step3", in -> {
                    var booking = new pl.training.workshop.m4.s09_extractclass.step3.Booking(
                            in.id(), in.name(), in.email(), in.phone(), in.amount());
                    return run(in, booking::pay, booking::summary);
                })
                .expect("nieopłacona, e-mail do normalizacji",
                        new BookingInput("B-1", "Anna Nowak", " Anna@Kino.PL ", "600 100 200",
                                new BigDecimal("74.00"), List.of()), """
                                Rezerwacja B-1
                                Klient: Anna Nowak <anna@kino.pl>, tel. 600-100-200
                                Kwota: 74.00
                                Platnosc: oczekuje
                                """)
                .expect("opłacona kartą, telefon z prefiksem",
                        new BookingInput("B-2", "Jan Kowalski", "jan@kino.pl", "+48 600-100-201",
                                new BigDecimal("31.60"), List.of("4111 1111 1111 1234")), """
                                Rezerwacja B-2
                                Klient: Jan Kowalski <jan@kino.pl>, tel. 600-100-201
                                Kwota: 31.60
                                Platnosc: oplacona karta **** 1234
                                """)
                .expect("druga płatność odrzucona",
                        new BookingInput("B-3", "Jan Kowalski", "jan@kino.pl", "600100202",
                                new BigDecimal("25.00"), List.of("4111 1111 1111 1234", "5500 0000 0000 0004")),
                        """
                                Rezerwacja B-3
                                Klient: Jan Kowalski <jan@kino.pl>, tel. 600-100-202
                                Kwota: 25.00
                                Platnosc: oplacona karta **** 1234
                                BLAD: Rezerwacja B-3 jest juz oplacona
                                """)
                .tests();
    }

    private static String run(BookingInput in, Consumer<String> pay, Supplier<String> summary) {
        StringBuilder errors = new StringBuilder();
        for (String card : in.cards()) {
            try {
                pay.accept(card);
            } catch (IllegalStateException e) {
                errors.append("BLAD: ").append(e.getMessage()).append('\n');
            }
        }
        return summary.get() + errors;
    }
}
