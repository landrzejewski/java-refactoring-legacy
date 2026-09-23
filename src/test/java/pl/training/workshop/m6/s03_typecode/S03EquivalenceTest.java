package pl.training.workshop.m6.s03_typecode;

import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Odczyt CSV, zachowanie formatu i zapis zwrotny tego samego kodu int. */
final class S03EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepReadsAndWritesTheSameCsv() {
        return Scene.<String, String>variants()
                .variant("start", safe(new pl.training.workshop.m6.s03_typecode.start.ScreeningCsv()::describe))
                .variant("step1", safe(new pl.training.workshop.m6.s03_typecode.step1.ScreeningCsv()::describe))
                .variant("step2", safe(new pl.training.workshop.m6.s03_typecode.step2.ScreeningCsv()::describe))
                .variant("step3", safe(new pl.training.workshop.m6.s03_typecode.step3.ScreeningCsv()::describe))
                .expect("2D", "Amator;1", "Amator|2D|25.00|okulary:nie|csv=Amator;1")
                .expect("3D", "Kraina Lodu;2", "Kraina Lodu|3D|32.00|okulary:tak|csv=Kraina Lodu;2")
                .expect("IMAX ze spacją", "Diuna; 3", "Diuna|IMAX|40.00|okulary:nie|csv=Diuna;3")
                .expect("nieznany kod", "Diuna;7", "ERROR: unknown format code: 7")
                .expect("kod 0", "Diuna;0", "ERROR: unknown format code: 0")
                .expect("kod nie jest liczbą", "Diuna;x", "ERROR: For input string: \"x\"")
                .tests();
    }

    private static Function<String, String> safe(Function<String, String> describe) {
        return line -> {
            try {
                return describe.apply(line);
            } catch (IllegalArgumentException exception) {
                return "ERROR: " + exception.getMessage();
            }
        };
    }
}
