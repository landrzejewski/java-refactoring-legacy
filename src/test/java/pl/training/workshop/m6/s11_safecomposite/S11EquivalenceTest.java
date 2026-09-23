package pl.training.workshop.m6.s11_safecomposite;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Opis i cena zestawów bez zmian - zmienia się tylko miejsce add() w typach. */
final class S11EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepDescribesCombosTheSame() {
        return Scene.<String, String>variants()
                .variant("start", c -> new pl.training.workshop.m6.s11_safecomposite.start.ComboCatalog().find(c).describe())
                .variant("step1", c -> new pl.training.workshop.m6.s11_safecomposite.step1.ComboCatalog().find(c).describe())
                .variant("step2", c -> new pl.training.workshop.m6.s11_safecomposite.step2.ComboCatalog().find(c).describe())
                .expect("zestaw rodzinny", "family",
                        "Zestaw Rodzinny 49.00 [Popcorn XL 24.00, Napoje 25.00 [Cola 9.00, Cola 9.00, Woda 7.00]]")
                .expect("zestaw duo", "duo", "Zestaw Duo 36.00 [Popcorn L 18.00, Cola 9.00, Cola 9.00]")
                .expect("pojedynczy produkt", "nachos", "Nachos 14.00")
                .tests();
    }
}
