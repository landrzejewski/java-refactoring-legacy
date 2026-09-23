package pl.training.workshop.m6.s02_polymorphism;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Jeden test kontraktowy dla wszystkich rodzajów seansu i wszystkich kroków. */
final class S02EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepDescribesScreeningsTheSame() {
        return Scene.<ScreeningRow, String>variants()
                .variant("start", row -> run(() -> {
                    var s = pl.training.workshop.m6.s02_polymorphism.start.Screening.fromRow(row);
                    return s.label() + "|" + s.durationMinutes() + "|" + s.price();
                }))
                .variant("step1", row -> run(() -> {
                    var s = pl.training.workshop.m6.s02_polymorphism.step1.Screening.fromRow(row);
                    return s.label() + "|" + s.durationMinutes() + "|" + s.price();
                }))
                .variant("step2", row -> run(() -> {
                    var s = pl.training.workshop.m6.s02_polymorphism.step2.Screening.fromRow(row);
                    return s.label() + "|" + s.durationMinutes() + "|" + s.price();
                }))
                .variant("step3", row -> run(() -> {
                    var s = pl.training.workshop.m6.s02_polymorphism.step3.Screening.fromRow(row);
                    return s.label() + "|" + s.durationMinutes() + "|" + s.price();
                }))
                .expect("zwykły seans", new ScreeningRow("REGULAR", "Amator", 120), "Amator|140|25.00")
                .expect("premiera", new ScreeningRow("PREMIERE", "Diuna", 166), "Premiera: Diuna|196|35.00")
                .expect("maraton 3 filmy", new ScreeningRow("MARATHON", "Wladca Pierscieni", 3),
                        "Maraton: Wladca Pierscieni (3 filmy)|390|60.00")
                .expect("maraton 1 film - bez przerw", new ScreeningRow("MARATHON", "Diuna", 1),
                        "Maraton: Diuna (1 filmy)|120|20.00")
                .expect("nieznany rodzaj", new ScreeningRow("DRIVE_IN", "Amator", 120),
                        "ERROR: unknown screening kind: DRIVE_IN")
                .tests();
    }

    private static String run(Supplier<String> description) {
        try {
            return description.get();
        } catch (IllegalArgumentException exception) {
            return "ERROR: " + exception.getMessage();
        }
    }
}
