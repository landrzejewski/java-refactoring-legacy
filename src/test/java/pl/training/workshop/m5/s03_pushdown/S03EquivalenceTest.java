package pl.training.workshop.m5.s03_pushdown;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Test równoważności: sprzedaż (z dopłatą VIP i bez) daje tę samą cenę w start i każdym kroku. */
final class S03EquivalenceTest {
    record Sale(String kind, String basePrice, boolean vip) {
    }

    @TestFactory
    Stream<DynamicTest> everyStepSellsForTheSamePrice() {
        return Scene.<Sale, String>variants()
                .variant("start", s -> new pl.training.workshop.m5.s03_pushdown.start.BoxOffice()
                        .sell(s.kind(), Money.of(s.basePrice()), s.vip()).toString())
                .variant("step1", s -> new pl.training.workshop.m5.s03_pushdown.step1.BoxOffice()
                        .sell(s.kind(), Money.of(s.basePrice()), s.vip()).toString())
                .variant("step2", s -> new pl.training.workshop.m5.s03_pushdown.step2.BoxOffice()
                        .sell(s.kind(), Money.of(s.basePrice()), s.vip()).toString())
                .variant("step3", s -> new pl.training.workshop.m5.s03_pushdown.step3.BoxOffice()
                        .sell(s.kind(), Money.of(s.basePrice()), s.vip()).toString())
                .expect("normalny 2D", new Sale("NORMAL", "25.00", false), "25.00")
                .expect("normalny 2D z dopłatą VIP", new Sale("NORMAL", "25.00", true), "35.00")
                .expect("studencki 3D", new Sale("STUDENT", "32.00", false), "24.00")
                .expect("studencki 3D - prośba o VIP ignorowana", new Sale("STUDENT", "32.00", true), "24.00")
                .tests();
    }
}
