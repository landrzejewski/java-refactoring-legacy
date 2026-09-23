package pl.training.workshop.m8.s05_boyscout;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/** Test różnicowy demaskuje "sprzątanie", które zmieniło zachowanie. */
final class S05SolutionTest {
    private final Map<String, Ticket> cases = new LinkedHashMap<>();

    {
        cases.put("zwykły bilet", S05EquivalenceTest.REGULAR);
        cases.put("miejsca w rzędach 9 i 10", S05EquivalenceTest.ROWS_9_AND_10);
        cases.put("brak telefonu", S05EquivalenceTest.NO_PHONE);
        cases.put("e-mail z wielkimi literami", S05EquivalenceTest.MIXED_CASE_EMAIL);
    }

    @Test
    void abusiveCleanupChangesBehaviourInThreeCases() {
        var before = new pl.training.workshop.m8.s05_boyscout.start.TicketPrinter();
        var abuse = new pl.training.workshop.m8.s05_boyscout.step1.TicketPrinter();
        List<String> changed = new ArrayList<>();
        cases.forEach((name, ticket) -> {
            if (!before.print(ticket).equals(abuse.print(ticket))) {
                changed.add(name);
            }
        });
        assertEquals(List.of("miejsca w rzędach 9 i 10", "brak telefonu", "e-mail z wielkimi literami"), changed);
    }

    @Test
    void abusiveCleanupSortsSeatsLexicographically() {
        String printed = new pl.training.workshop.m8.s05_boyscout.step1.TicketPrinter()
                .print(S05EquivalenceTest.ROWS_9_AND_10);
        assertEquals("Miejsca: A10, A9", printed.lines().filter(l -> l.startsWith("Miejsca")).findFirst().orElseThrow());
    }

    @Test
    void correctBoyScoutStepChangesNothingObservable() {
        var before = new pl.training.workshop.m8.s05_boyscout.start.TicketPrinter();
        var after = new pl.training.workshop.m8.s05_boyscout.step2.TicketPrinter();
        cases.forEach((name, ticket) -> assertEquals(before.print(ticket), after.print(ticket), name));
    }
}
