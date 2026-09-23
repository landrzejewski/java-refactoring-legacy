package pl.training.workshop.m3.s14_reversiblepattern;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Model procentowy rozlicza się tak samo przed wzorcem, ze wzorcem i po jego usunięciu. */
final class S14EquivalenceTest {
    record Case(int week, String revenue) {
    }

    private static final Deal DUNE = new Deal("Diuna", "PERCENT");

    @TestFactory
    Stream<DynamicTest> percentageDealsSettleTheSame() {
        return Scene.<Case, String>variants()
                .variant("start", c -> new pl.training.workshop.m3.s14_reversiblepattern.start.DistributorSettlement()
                        .payout(DUNE, c.week(), new BigDecimal(c.revenue())).toString())
                .variant("step1", c -> new pl.training.workshop.m3.s14_reversiblepattern.step1.DistributorSettlement()
                        .payout(DUNE, c.week(), new BigDecimal(c.revenue())).toString())
                .variant("step2", c -> new pl.training.workshop.m3.s14_reversiblepattern.step2.DistributorSettlement()
                        .payout(DUNE, c.week(), new BigDecimal(c.revenue())).toString())
                .variant("step3", c -> new pl.training.workshop.m3.s14_reversiblepattern.step3.DistributorSettlement()
                        .payout(DUNE, c.week(), new BigDecimal(c.revenue())).toString())
                .expect("tydzien 1: 50%", new Case(1, "2000.00"), "1000.00")
                .expect("tydzien 2: 40%", new Case(2, "2000.00"), "800.00")
                .expect("tydzien 3: 35% ponizej gwarancji", new Case(3, "1000.00"), "500.00")
                .expect("tydzien 5: 35%", new Case(5, "4000.00"), "1400.00")
                .tests();
    }
}
