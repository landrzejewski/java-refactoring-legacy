package pl.training.workshop.m3.s16_temporalcoupling;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Wydruk jest identyczny; znika tylko możliwość złego wywołania. */
final class S16EquivalenceTest {
    record Case(Screening screening, int seat, String buyer) {
    }

    private static final Screening DUNE = new Screening("Diuna", "IMAX", LocalDateTime.of(2026, 10, 2, 20, 0));

    @TestFactory
    Stream<DynamicTest> everyStepPrintsTheSameTicket() {
        return Scene.<Case, String>variants()
                .variant("start", c -> new pl.training.workshop.m3.s16_temporalcoupling.start.TicketDesk()
                        .issue(c.screening(), c.seat(), c.buyer()))
                .variant("step1", c -> new pl.training.workshop.m3.s16_temporalcoupling.step1.TicketDesk()
                        .issue(c.screening(), c.seat(), c.buyer()))
                .variant("step2", c -> new pl.training.workshop.m3.s16_temporalcoupling.step2.TicketDesk()
                        .issue(c.screening(), c.seat(), c.buyer()))
                .expect("Diuna IMAX", new Case(DUNE, 14, "Anna@Kino.pl"),
                        "BILET Diuna (IMAX) 2026-10-02T20:00, miejsce 14, dla anna@kino.pl")
                .expect("Kraina Lodu rano",
                        new Case(new Screening("Kraina Lodu", "3D", LocalDateTime.of(2026, 10, 3, 10, 0)), 3,
                                "jan@kino.pl"),
                        "BILET Kraina Lodu (3D) 2026-10-03T10:00, miejsce 3, dla jan@kino.pl")
                .tests();
    }

    @Test
    void step2RejectsIncompleteRequestAtCreation() {
        assertThrows(NullPointerException.class,
                () -> new pl.training.workshop.m3.s16_temporalcoupling.step2.TicketRequest(DUNE, 1, null));
    }
}
