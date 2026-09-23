package pl.training.workshop.m3.s09_lsp;

import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Kasa (na zwykłej sali) i raport (na sali archiwalnej) działają tak samo we wszystkich wariantach. */
final class S09EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> sellingAndReportingBehaveTheSame() {
        return Scene.<Integer, String>variants()
                .variant("start", seat -> {
                    var hall = new pl.training.workshop.m3.s09_lsp.start.Hall(10);
                    hall.reserve(1);
                    return new pl.training.workshop.m3.s09_lsp.start.BoxOffice().sell(hall, seat) + " | "
                            + new pl.training.workshop.m3.s09_lsp.start.OccupancyReport().describe(
                            new pl.training.workshop.m3.s09_lsp.start.ReadOnlyHall(10, Set.of(1, 2, seat)));
                })
                .variant("step1", seat -> {
                    var hall = new pl.training.workshop.m3.s09_lsp.step1.Hall(10);
                    hall.reserve(1);
                    return new pl.training.workshop.m3.s09_lsp.step1.BoxOffice().sell(hall, seat) + " | "
                            + new pl.training.workshop.m3.s09_lsp.step1.OccupancyReport().describe(
                            new pl.training.workshop.m3.s09_lsp.step1.ReadOnlyHall(10, Set.of(1, 2, seat)));
                })
                .variant("step2", seat -> {
                    var hall = new pl.training.workshop.m3.s09_lsp.step2.Hall(10);
                    hall.reserve(1);
                    return new pl.training.workshop.m3.s09_lsp.step2.BoxOffice().sell(hall, seat) + " | "
                            + new pl.training.workshop.m3.s09_lsp.step2.OccupancyReport().describe(
                            new pl.training.workshop.m3.s09_lsp.step2.ReadOnlyHall(10, Set.of(1, 2, seat)));
                })
                .expect("miejsce 5", 5, "sprzedano miejsce 5, wolnych: 8 | zajete 3 z 10")
                .expect("ostatnie miejsce", 10, "sprzedano miejsce 10, wolnych: 8 | zajete 3 z 10")
                .tests();
    }
}
