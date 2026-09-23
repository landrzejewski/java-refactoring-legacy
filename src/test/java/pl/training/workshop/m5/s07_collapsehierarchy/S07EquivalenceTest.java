package pl.training.workshop.m5.s07_collapsehierarchy;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: opis sal i reguła VIP identyczne w start i każdym kroku. */
final class S07EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepDescribesHallsTheSameWay() {
        return Scene.<String, String>variants()
                .variant("start", new pl.training.workshop.m5.s07_collapsehierarchy.start.HallCatalog()::describe)
                .variant("step1", new pl.training.workshop.m5.s07_collapsehierarchy.step1.HallCatalog()::describe)
                .variant("step2", new pl.training.workshop.m5.s07_collapsehierarchy.step2.HallCatalog()::describe)
                .variant("step3", new pl.training.workshop.m5.s07_collapsehierarchy.step3.HallCatalog()::describe)
                .expect("zwykła sala", "Sala 1", "Sala 1: 180 miejsc, VIP od rzędu 10")
                .expect("sala IMAX - VIP w dwóch ostatnich rzędach", "Sala IMAX",
                        "Sala IMAX: 308 miejsc, VIP od rzędu 13")
                .expect("nieznana sala", "Sala 9", "brak sali: Sala 9")
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> everyStepAppliesTheSameVipRule() {
        return Scene.<Integer, Boolean>variants()
                .variant("start", row -> new pl.training.workshop.m5.s07_collapsehierarchy.start.HallCatalog().isVip("Sala IMAX", row))
                .variant("step1", row -> new pl.training.workshop.m5.s07_collapsehierarchy.step1.HallCatalog().isVip("Sala IMAX", row))
                .variant("step2", row -> new pl.training.workshop.m5.s07_collapsehierarchy.step2.HallCatalog().isVip("Sala IMAX", row))
                .variant("step3", row -> new pl.training.workshop.m5.s07_collapsehierarchy.step3.HallCatalog().isVip("Sala IMAX", row))
                .expect("rząd 12 w IMAX - zwykły", 12, false)
                .expect("rząd 13 w IMAX - VIP", 13, true)
                .tests();
    }
}
