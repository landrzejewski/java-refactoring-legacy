package pl.training.workshop.m6.s17_singleton;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Wejście "FORMAT kanał": wycena bez zmian niezależnie od liczby instancji cennika. */
final class S17EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepQuotesTheSame() {
        return Scene.<String, String>variants()
                .variant("start", safe(new pl.training.workshop.m6.s17_singleton.start.TicketDesk()::quote))
                .variant("step1", safe(new pl.training.workshop.m6.s17_singleton.step1.TicketDesk()::quote))
                .variant("step2", safe(new pl.training.workshop.m6.s17_singleton.step2.TicketDesk()::quote))
                .variant("step3", safe(new pl.training.workshop.m6.s17_singleton.step3.TicketDesk()::quote))
                .expect("2D kasa", "2D kasa", "25.00")
                .expect("3D online", "3D online", "34.00")
                .expect("IMAX online", "IMAX online", "42.00")
                .expect("nieznany format", "4DX kasa", "ERROR unknown format: 4DX")
                .tests();
    }

    private static Function<String, String> safe(BiFunction<String, Boolean, Money> quote) {
        return input -> {
            String[] parts = input.split(" ");
            try {
                return quote.apply(parts[0], parts[1].equals("online")).toString();
            } catch (IllegalArgumentException exception) {
                return "ERROR " + exception.getMessage();
            }
        };
    }
}
