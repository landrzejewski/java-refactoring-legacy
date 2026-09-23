package pl.training.workshop.m6.s12_onemany;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/**
 * Klient (CancellationDesk) zwraca jeden bilet albo listę. W start rozróżnia refund/refundAll,
 * w kroku 3 zawsze buduje TicketGroup (także z jednego biletu) - wynik jest ten sam.
 */
final class S12EquivalenceTest {
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 2, 12, 0);

    @TestFactory
    Stream<DynamicTest> everyStepRefundsTheSame() {
        return Scene.<List<TicketData>, String>variants()
                .variant("start", tickets -> new pl.training.workshop.m6.s12_onemany.start.CancellationDesk()
                        .refund(tickets, NOW).toString())
                .variant("step1", tickets -> new pl.training.workshop.m6.s12_onemany.step1.CancellationDesk()
                        .refund(tickets, NOW).toString())
                .variant("step2", tickets -> new pl.training.workshop.m6.s12_onemany.step2.CancellationDesk()
                        .refund(tickets, NOW).toString())
                .variant("step3", tickets -> new pl.training.workshop.m6.s12_onemany.step3.CancellationDesk()
                        .refund(tickets, NOW).toString())
                .expect("jeden, ponad 24h", List.of(ticket("40.00", 2, 18, 0)), "37.00")
                .expect("jeden, dokładnie 24h", List.of(ticket("25.00", 1, 12, 0)), "22.00")
                .expect("jeden, 23h59m - 50%", List.of(ticket("25.00", 1, 11, 59)), "9.50")
                .expect("jeden, po starcie - potrącenie nie schodzi poniżej 0",
                        List.of(ticket("25.00", 0, 11, 0)), "0.00")
                .expect("wiele - potrącenie raz", List.of(
                        ticket("40.00", 2, 18, 0), ticket("32.00", 0, 20, 0), ticket("25.00", 0, 11, 0)), "53.00")
                .expect("pusta lista", List.of(), "0.00")
                .tests();
    }

    private static TicketData ticket(String price, int daysAfter, int hour, int minute) {
        return new TicketData(Money.of(price), NOW.toLocalDate().plusDays(daysAfter).atTime(hour, minute));
    }
}
