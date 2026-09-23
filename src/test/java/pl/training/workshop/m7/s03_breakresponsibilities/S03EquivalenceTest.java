package pl.training.workshop.m7.s03_breakresponsibilities;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: ten sam wynik i ta sama skrzynka nadawcza w start i każdym kroku. */
final class S03EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepBooksAndNotifiesTheSame() {
        return Scene.<BookingRequest, String>variants()
                .variant("start", observe((outbox, request) ->
                        new pl.training.workshop.m7.s03_breakresponsibilities.start.BookingDesk(outbox).book(request)))
                .variant("step1", observe((outbox, request) ->
                        new pl.training.workshop.m7.s03_breakresponsibilities.step1.BookingDesk(outbox).book(request)))
                .variant("step2", observe((outbox, request) ->
                        new pl.training.workshop.m7.s03_breakresponsibilities.step2.BookingDesk(outbox).book(request)))
                .variant("step3", observe((outbox, request) ->
                        new pl.training.workshop.m7.s03_breakresponsibilities.step3.BookingDesk(outbox).book(request)))
                .expect("IMAX, jedno miejsce VIP",
                        new BookingRequest("anna@kino.pl", "IMAX", List.of("A5", "C10")),
                        "OK 90.00 | [anna@kino.pl: Rezerwacja 2 miejsc (VIP: 1), do zaplaty 90.00]")
                .expect("2D bez VIP",
                        new BookingRequest("jan@kino.pl", "2D", List.of("B3")),
                        "OK 25.00 | [jan@kino.pl: Rezerwacja 1 miejsc, do zaplaty 25.00]")
                .expect("niepoprawny e-mail ma pierwszenstwo",
                        new BookingRequest("jan-kino.pl", "2D", List.of()),
                        "ERROR: niepoprawny e-mail | []")
                .expect("brak miejsc",
                        new BookingRequest("jan@kino.pl", "3D", List.of()),
                        "ERROR: brak miejsc | []")
                .expect("niepoprawne miejsce",
                        new BookingRequest("jan@kino.pl", "3D", List.of("A1", "Z99")),
                        "ERROR: niepoprawne miejsce Z99 | []")
                .tests();
    }

    private static Function<BookingRequest, String> observe(BiFunction<Outbox, BookingRequest, String> desk) {
        return request -> {
            Outbox outbox = new Outbox();
            String result = desk.apply(outbox, request);
            return result + " | " + outbox.sent();
        };
    }
}
