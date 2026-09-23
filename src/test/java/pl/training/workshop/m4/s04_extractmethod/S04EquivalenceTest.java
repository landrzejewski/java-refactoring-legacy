package pl.training.workshop.m4.s04_extractmethod;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: start i każdy krok dają identyczny dokument. */
final class S04EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepDescribesTicketsTheSameWay() {
        return Scene.<Order, String>variants()
                .variant("start", new pl.training.workshop.m4.s04_extractmethod.start.TicketSummary()::describe)
                .variant("step1", new pl.training.workshop.m4.s04_extractmethod.step1.TicketSummary()::describe)
                .variant("step2", new pl.training.workshop.m4.s04_extractmethod.step2.TicketSummary()::describe)
                .variant("step3", new pl.training.workshop.m4.s04_extractmethod.step3.TicketSummary()::describe)
                .expect("IMAX wieczorem, jedno miejsce VIP",
                        new Order("Diuna", "IMAX", LocalTime.of(20, 0), List.of(5, 10)),
                        """
                                BILETY: Diuna
                                Format: IMAX, start 20:00
                                Miejsc: 2 (w tym VIP: 1)
                                Razem: 90.00
                                """)
                .expect("3D rano bez VIP",
                        new Order("Kraina Lodu", "3D", LocalTime.of(11, 0), List.of(1, 2, 3)),
                        """
                                BILETY: Kraina Lodu
                                Format: 3D, start 11:00
                                Miejsc: 3
                                Razem: 81.00
                                """)
                .expect("2D, same miejsca VIP",
                        new Order("Amator", "2D", LocalTime.of(18, 30), List.of(10, 11)),
                        """
                                BILETY: Amator
                                Format: 2D, start 18:30
                                Miejsc: 2 (w tym VIP: 2)
                                Razem: 70.00
                                """)
                .expect("puste zamówienie",
                        new Order("Amator", "2D", LocalTime.of(18, 30), List.of()),
                        """
                                BILETY: Amator
                                Format: 2D, start 18:30
                                Miejsc: 0
                                Razem: 0.00
                                """)
                .tests();
    }
}
