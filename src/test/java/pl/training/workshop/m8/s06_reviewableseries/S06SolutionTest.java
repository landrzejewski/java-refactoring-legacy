package pl.training.workshop.m8.s06_reviewableseries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import pl.training.workshop.shared.Money;

/**
 * Commit 3 zmienia zachowanie - test pokazuje nową regułę i to, że zmienia ona WYŁĄCZNIE
 * bilety NORMAL we wtorek (różnica zachowania między commitem 2 a 3 na siatce przypadków).
 */
final class S06SolutionTest {
    private final Function<TicketQuery, Money> before =
            new pl.training.workshop.m8.s06_reviewableseries.step2.PriceList()::price;
    private final Function<TicketQuery, Money> after =
            new pl.training.workshop.m8.s06_reviewableseries.step3.PriceList()::price;

    @Test
    void cheapTuesdayGivesNormalTicketTwentyPercentOff() {
        assertEquals(Money.of("20.00"), after.apply(new TicketQuery("2D", "NORMAL", S06EquivalenceTest.TUESDAY_EVENING, 5)));
        // IMAX: 40 - 20% = 32, rano -5, VIP +10
        assertEquals(Money.of("37.00"), after.apply(new TicketQuery("IMAX", "NORMAL", S06EquivalenceTest.TUESDAY_MORNING, 12)));
        assertEquals(Money.of("25.00"), after.apply(new TicketQuery("2D", "NORMAL", S06EquivalenceTest.MONDAY_EVENING, 5)));
        assertEquals(Money.of("18.75"), after.apply(new TicketQuery("2D", "STUDENT", S06EquivalenceTest.TUESDAY_EVENING, 5)));
    }

    @Test
    void behaviourChangeIsLimitedToNormalTicketsOnTuesday() {
        List<TicketQuery> changed = new ArrayList<>();
        int checked = 0;
        for (String format : List.of("2D", "3D", "IMAX")) {
            for (String type : List.of("NORMAL", "STUDENT", "SENIOR", "CHILD")) {
                for (int day = 9; day <= 15; day++) {
                    for (int hour : List.of(10, 18)) {
                        for (int row : List.of(1, 10)) {
                            TicketQuery query = new TicketQuery(format, type, LocalDateTime.of(2026, 3, day, hour, 0), row);
                            checked++;
                            if (!before.apply(query).equals(after.apply(query))) {
                                changed.add(query);
                            }
                        }
                    }
                }
            }
        }
        assertEquals(336, checked);
        assertEquals(3 * 2 * 2, changed.size(), "3 formaty x 2 pory x 2 rzędy");
        assertTrue(changed.stream().allMatch(q -> q.type().equals("NORMAL")
                && q.start().getDayOfWeek() == DayOfWeek.TUESDAY), changed.toString());
    }
}
