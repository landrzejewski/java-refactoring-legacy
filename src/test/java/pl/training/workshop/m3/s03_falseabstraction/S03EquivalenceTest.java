package pl.training.workshop.m3.s03_falseabstraction;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Rozbicie fałszywej abstrakcji nie zmienia cen biletów ani karnetów. */
final class S03EquivalenceTest {
    record TicketCase(String format, boolean morning, boolean ownGlasses) {
    }

    @TestFactory
    Stream<DynamicTest> ticketsCostTheSame() {
        return Scene.<TicketCase, String>variants()
                .variant("start", c -> new pl.training.workshop.m3.s03_falseabstraction.start.TicketCounter()
                        .ticket(c.format(), c.morning(), c.ownGlasses()).toString())
                .variant("step1", c -> new pl.training.workshop.m3.s03_falseabstraction.step1.TicketCounter()
                        .ticket(c.format(), c.morning(), c.ownGlasses()).toString())
                .variant("step2", c -> new pl.training.workshop.m3.s03_falseabstraction.step2.TicketCounter()
                        .ticket(c.format(), c.morning(), c.ownGlasses()).toString())
                .expect("3D rano, bez wlasnych okularow", new TicketCase("3D", true, false), "30.00")
                .expect("3D wieczorem, wlasne okulary", new TicketCase("3D", false, true), "32.00")
                .expect("IMAX wieczorem", new TicketCase("IMAX", false, false), "40.00")
                .expect("2D rano", new TicketCase("2D", true, false), "20.00")
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> passesCostTheSame() {
        return Scene.<Integer, String>variants()
                .variant("start", n -> new pl.training.workshop.m3.s03_falseabstraction.start.PassCounter()
                        .pass(n).toString())
                .variant("step1", n -> new pl.training.workshop.m3.s03_falseabstraction.step1.PassCounter()
                        .pass(n).toString())
                .variant("step2", n -> new pl.training.workshop.m3.s03_falseabstraction.step2.PassCounter()
                        .pass(n).toString())
                .expect("karnet na 10 wejsc", 10, "200.00")
                .expect("karnet na 5 wejsc", 5, "100.00")
                .tests();
    }
}
