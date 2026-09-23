package pl.training.workshop.m5.s05_extractsubclass;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: linia repertuaru (opis i cena) jest identyczna w start i każdym kroku. */
final class S05EquivalenceTest {
    record Entry(String title, String format, String guest) {
    }

    @TestFactory
    Stream<DynamicTest> everyStepPrintsTheSameProgramme() {
        return Scene.<Entry, String>variants()
                .variant("start", e -> new pl.training.workshop.m5.s05_extractsubclass.start.Programme().line(e.title(), e.format(), e.guest()))
                .variant("step1", e -> new pl.training.workshop.m5.s05_extractsubclass.step1.Programme().line(e.title(), e.format(), e.guest()))
                .variant("step2", e -> new pl.training.workshop.m5.s05_extractsubclass.step2.Programme().line(e.title(), e.format(), e.guest()))
                .variant("step3", e -> new pl.training.workshop.m5.s05_extractsubclass.step3.Programme().line(e.title(), e.format(), e.guest()))
                .variant("step4", e -> new pl.training.workshop.m5.s05_extractsubclass.step4.Programme().line(e.title(), e.format(), e.guest()))
                .expect("zwykły seans IMAX", new Entry("Diuna", "IMAX", null), "Diuna (IMAX) | 40.00")
                .expect("premiera 2D", new Entry("Amator", "2D", "Anna Nowak"),
                        "Amator (2D) - premiera, gość: Anna Nowak | 40.00")
                .expect("premiera 3D", new Entry("Kraina Lodu", "3D", "Jan Kowalski"),
                        "Kraina Lodu (3D) - premiera, gość: Jan Kowalski | 47.00")
                .tests();
    }
}
