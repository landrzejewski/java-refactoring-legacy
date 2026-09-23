package pl.training.workshop.m5.s12_bridgemethods;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Test równoważności: cena i lista obsługiwanych typów identyczne w start i każdym kroku. */
final class S12EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepPricesAndReportsTheSameWay() {
        return Scene.<Ticket, String>variants()
                .variant("start", t -> {
                    var registry = pl.training.workshop.m5.s12_bridgemethods.start.RuleRegistry.standard();
                    return registry.price(t) + " " + registry.supportedTypes();
                })
                .variant("step1", t -> {
                    var registry = pl.training.workshop.m5.s12_bridgemethods.step1.RuleRegistry.standard();
                    return registry.price(t) + " " + registry.supportedTypes();
                })
                .variant("step2", t -> {
                    var registry = pl.training.workshop.m5.s12_bridgemethods.step2.RuleRegistry.standard();
                    return registry.price(t) + " " + registry.supportedTypes();
                })
                .expect("normalny IMAX", new StandardTicket(Money.of("40.00")),
                        "40.00 [StandardTicket, StudentTicket]")
                .expect("studencki 2D", new StudentTicket(Money.of("25.00"), "S-123"),
                        "18.75 [StandardTicket, StudentTicket]")
                .tests();
    }
}
