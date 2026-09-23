package pl.training.workshop.m6.s01_strategy;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Tabela decyzji programu zniżek: każda gałąź, typ nieznany, program nieznany i null. */
final class S01EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepPricesTheSame() {
        return Scene.<PriceRequest, String>variants()
                .variant("start", r -> run(() -> new pl.training.workshop.m6.s01_strategy.start.PriceBoard().priceFor(r)))
                .variant("step1", r -> run(() -> new pl.training.workshop.m6.s01_strategy.step1.PriceBoard().priceFor(r)))
                .variant("step2", r -> run(() -> new pl.training.workshop.m6.s01_strategy.step2.PriceBoard().priceFor(r)))
                .variant("step3", r -> run(() -> new pl.training.workshop.m6.s01_strategy.step3.PriceBoard().priceFor(r)))
                .expect("STANDARD normalny 2D", request("25.00", "N", "STANDARD"), "25.00")
                .expect("STANDARD student 3D", request("32.00", "S", "STANDARD"), "24.00")
                .expect("STANDARD senior IMAX", request("40.00", "E", "STANDARD"), "28.00")
                .expect("STANDARD dziecko 2D", request("25.00", "C", "STANDARD"), "15.00")
                .expect("STUDENT_WEEK student IMAX", request("40.00", "S", "STUDENT_WEEK"), "20.00")
                .expect("STUDENT_WEEK dziecko 3D", request("32.00", "C", "STUDENT_WEEK"), "19.20")
                .expect("PREMIERE dziecko IMAX", request("40.00", "C", "PREMIERE"), "40.00")
                .expect("PREMIERE nie sprawdza typu", request("40.00", "X", "PREMIERE"), "40.00")
                .expect("STANDARD nieznany typ", request("25.00", "X", "STANDARD"),
                        "ERROR: unknown ticket type: X")
                .expect("nieznany program", request("25.00", "N", "BLACK_FRIDAY"),
                        "ERROR: unknown program: BLACK_FRIDAY")
                .expect("program null", request("25.00", "N", null), "ERROR: program must not be null")
                .tests();
    }

    private static PriceRequest request(String base, String type, String program) {
        return new PriceRequest(Money.of(base), type, program);
    }

    static String run(Supplier<Money> price) {
        try {
            return price.get().toString();
        } catch (IllegalArgumentException exception) {
            return "ERROR: " + exception.getMessage();
        }
    }
}
