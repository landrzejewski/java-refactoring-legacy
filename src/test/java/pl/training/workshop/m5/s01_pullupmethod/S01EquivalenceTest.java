package pl.training.workshop.m5.s01_pullupmethod;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Test równoważności: start i każdy krok drukują identyczne etykiety biletów. */
final class S01EquivalenceTest {
    record Sale(String kind, String title, String basePrice) {
    }

    @TestFactory
    Stream<DynamicTest> everyStepLabelsTicketsTheSameWay() {
        return Scene.<Sale, String>variants()
                .variant("start", s -> new pl.training.workshop.m5.s01_pullupmethod.start.BoxOffice()
                        .label(s.kind(), s.title(), Money.of(s.basePrice())))
                .variant("step1", s -> new pl.training.workshop.m5.s01_pullupmethod.step1.BoxOffice()
                        .label(s.kind(), s.title(), Money.of(s.basePrice())))
                .variant("step2", s -> new pl.training.workshop.m5.s01_pullupmethod.step2.BoxOffice()
                        .label(s.kind(), s.title(), Money.of(s.basePrice())))
                .variant("step3", s -> new pl.training.workshop.m5.s01_pullupmethod.step3.BoxOffice()
                        .label(s.kind(), s.title(), Money.of(s.basePrice())))
                .expect("normalny IMAX", new Sale("NORMAL", "Diuna", "40.00"), "Diuna: 40.00")
                .expect("studencki 2D (-25%)", new Sale("STUDENT", "Amator", "25.00"), "Amator: 18.75")
                .expect("VIP 3D (+10.00)", new Sale("VIP", "Kraina Lodu", "32.00"), "Kraina Lodu: 42.00")
                .tests();
    }
}
