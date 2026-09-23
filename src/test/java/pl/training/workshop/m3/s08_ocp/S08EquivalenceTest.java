package pl.training.workshop.m3.s08_ocp;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Dla istniejących formatów start i każdy krok dają tę samą cenę i etykietę. */
final class S08EquivalenceTest {
    record Case(String format, boolean ownGlasses) {
    }

    @TestFactory
    Stream<DynamicTest> existingFormatsBehaveTheSame() {
        return Scene.<Case, String>variants()
                .variant("start", c -> {
                    var offer = new pl.training.workshop.m3.s08_ocp.start.ScreeningOffer();
                    return offer.label(c.format()) + ": " + offer.price(c.format(), c.ownGlasses());
                })
                .variant("step1", c -> {
                    var offer = new pl.training.workshop.m3.s08_ocp.step1.ScreeningOffer();
                    return offer.label(c.format()) + ": " + offer.price(c.format(), c.ownGlasses());
                })
                .variant("step2", c -> {
                    var offer = new pl.training.workshop.m3.s08_ocp.step2.ScreeningOffer();
                    return offer.label(c.format()) + ": " + offer.price(c.format(), c.ownGlasses());
                })
                .variant("step3", c -> {
                    var offer = new pl.training.workshop.m3.s08_ocp.step3.ScreeningOffer();
                    return offer.label(c.format()) + ": " + offer.price(c.format(), c.ownGlasses());
                })
                .expect("2D", new Case("2D", false), "2D: 25.00")
                .expect("3D z wypozyczeniem okularow", new Case("3D", false), "3D - okulary: 35.00")
                .expect("3D z wlasnymi okularami", new Case("3D", true), "3D - okulary: 32.00")
                .expect("IMAX", new Case("IMAX", false), "IMAX - ekran laserowy: 40.00")
                .tests();
    }
}
