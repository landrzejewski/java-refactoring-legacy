package pl.training.workshop.m8.s13_livingdocs;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: praca nad dokumentacją nie zmienia routingu. */
final class S13EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> routingStaysTheSame() {
        return Scene.<String, String>variants()
                .variant("start", op -> pl.training.workshop.m8.s13_livingdocs.start.Routing.routes().stream()
                        .filter(r -> r.operation().equals(op)).findFirst().orElseThrow().target())
                .variant("step1", op -> pl.training.workshop.m8.s13_livingdocs.step1.Routing.routes().stream()
                        .filter(r -> r.operation().equals(op)).findFirst().orElseThrow().target())
                .variant("step2", op -> pl.training.workshop.m8.s13_livingdocs.step2.Routing.routes().stream()
                        .filter(r -> r.operation().equals(op)).findFirst().orElseThrow().target())
                .expect("rezerwacja", "book", "new")
                .expect("raport", "report", "new")
                .expect("anulowanie", "cancel", "legacy")
                .tests();
    }
}
