package pl.training.workshop.m8.s01_branchbyabstraction;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.m8.s01_branchbyabstraction.step3.PricingMode;
import pl.training.workshop.support.Scene;

/** Test równoważności: każdy krok (i obie gałęzie przełącznika w kroku 3) potwierdza tak samo. */
final class S01EquivalenceTest {
    static final Screening DIUNA = new Screening("Diuna", 3, LocalDateTime.of(2026, 3, 13, 20, 0), 10);
    static final Screening KRAINA_LODU =
            new Screening("Kraina Lodu", 2, LocalDateTime.of(2026, 3, 14, 10, 30), 10);
    static final Screening AMATOR = new Screening("Amator", 1, LocalDateTime.of(2026, 3, 14, 18, 0), 10);
    static final Screening AMATOR_RANO = new Screening("Amator", 1, LocalDateTime.of(2026, 3, 15, 9, 0), 10);

    static final List<String> GROUP_SEATS =
            List.of("A1", "B1", "C1", "D1", "E1", "F1", "G1", "H1", "I1", "J1");

    @TestFactory
    Stream<DynamicTest> everyStepConfirmsTheSameWay() {
        return Scene.<BookingRequest, String>variants()
                .variant("start", new pl.training.workshop.m8.s01_branchbyabstraction.start.BookingService()::confirm)
                .variant("step1", new pl.training.workshop.m8.s01_branchbyabstraction.step1.BookingService()::confirm)
                .variant("step2", new pl.training.workshop.m8.s01_branchbyabstraction.step2.BookingService()::confirm)
                .variant("step3 LEGACY", new pl.training.workshop.m8.s01_branchbyabstraction.step3.BookingService(
                        PricingMode.LEGACY)::confirm)
                .variant("step3 MODERN", new pl.training.workshop.m8.s01_branchbyabstraction.step3.BookingService(
                        PricingMode.MODERN)::confirm)
                .variant("step4", new pl.training.workshop.m8.s01_branchbyabstraction.step4.BookingService()::confirm)
                .expect("IMAX online, student na miejscu VIP",
                        new BookingRequest(DIUNA, List.of("A5", "A10"), List.of("N", "S"), true, false),
                        "Diuna: A5,A10 - do zaplaty 84.00")
                .expect("3D rano w kasie, dziecko i normalny, okulary z wypożyczalni",
                        new BookingRequest(KRAINA_LODU, List.of("B1", "B2"), List.of("C", "N"), false, false),
                        "Kraina Lodu: B1,B2 - do zaplaty 47.20")
                .expect("3D rano online, własne okulary",
                        new BookingRequest(KRAINA_LODU, List.of("B1", "B2"), List.of("C", "N"), true, true),
                        "Kraina Lodu: B1,B2 - do zaplaty 45.20")
                .expect("grupa 10 biletów 2D online",
                        new BookingRequest(AMATOR, GROUP_SEATS, Collections.nCopies(10, "N"), true, false),
                        "Amator: A1,B1,C1,D1,E1,F1,G1,H1,I1,J1 - do zaplaty 245.00")
                .expect("senior rano na miejscu VIP",
                        new BookingRequest(AMATOR_RANO, List.of("C12"), List.of("E"), false, false),
                        "Amator: C12 - do zaplaty 22.50")
                .tests();
    }
}
