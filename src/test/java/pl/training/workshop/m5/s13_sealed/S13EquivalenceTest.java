package pl.training.workshop.m5.s13_sealed;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Test równoważności: dla znanych typów biletów cena jest identyczna w start i każdym kroku. */
final class S13EquivalenceTest {
    record Sale(String kind, String basePrice) {
    }

    @TestFactory
    Stream<DynamicTest> everyStepPricesKnownTicketsTheSameWay() {
        return Scene.<Sale, String>variants()
                .variant("start", s -> {
                    Money base = Money.of(s.basePrice());
                    pl.training.workshop.m5.s13_sealed.start.Ticket t = switch (s.kind()) {
                        case "STUDENT" -> new pl.training.workshop.m5.s13_sealed.start.StudentTicket(base);
                        case "SENIOR" -> new pl.training.workshop.m5.s13_sealed.start.SeniorTicket(base);
                        default -> new pl.training.workshop.m5.s13_sealed.start.StandardTicket(base);
                    };
                    return new pl.training.workshop.m5.s13_sealed.start.PriceCalculator().price(t).toString();
                })
                .variant("step1", s -> {
                    Money base = Money.of(s.basePrice());
                    pl.training.workshop.m5.s13_sealed.step1.Ticket t = switch (s.kind()) {
                        case "STUDENT" -> new pl.training.workshop.m5.s13_sealed.step1.StudentTicket(base);
                        case "SENIOR" -> new pl.training.workshop.m5.s13_sealed.step1.SeniorTicket(base);
                        default -> new pl.training.workshop.m5.s13_sealed.step1.StandardTicket(base);
                    };
                    return new pl.training.workshop.m5.s13_sealed.step1.PriceCalculator().price(t).toString();
                })
                .variant("step2", s -> {
                    Money base = Money.of(s.basePrice());
                    pl.training.workshop.m5.s13_sealed.step2.Ticket t = switch (s.kind()) {
                        case "STUDENT" -> new pl.training.workshop.m5.s13_sealed.step2.StudentTicket(base);
                        case "SENIOR" -> new pl.training.workshop.m5.s13_sealed.step2.SeniorTicket(base);
                        default -> new pl.training.workshop.m5.s13_sealed.step2.StandardTicket(base);
                    };
                    return new pl.training.workshop.m5.s13_sealed.step2.PriceCalculator().price(t).toString();
                })
                .variant("step3", s -> {
                    Money base = Money.of(s.basePrice());
                    pl.training.workshop.m5.s13_sealed.step3.Ticket t = switch (s.kind()) {
                        case "STUDENT" -> new pl.training.workshop.m5.s13_sealed.step3.StudentTicket(base);
                        case "SENIOR" -> new pl.training.workshop.m5.s13_sealed.step3.SeniorTicket(base);
                        default -> new pl.training.workshop.m5.s13_sealed.step3.StandardTicket(base);
                    };
                    return new pl.training.workshop.m5.s13_sealed.step3.PriceCalculator().price(t).toString();
                })
                .expect("normalny 2D", new Sale("NORMAL", "25.00"), "25.00")
                .expect("studencki 3D", new Sale("STUDENT", "32.00"), "24.00")
                .expect("senior IMAX", new Sale("SENIOR", "40.00"), "28.00")
                .tests();
    }
}
