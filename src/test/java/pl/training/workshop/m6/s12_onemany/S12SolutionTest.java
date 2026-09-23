package pl.training.workshop.m6.s12_onemany;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m6.s12_onemany.step3.RefundService;
import pl.training.workshop.m6.s12_onemany.step3.SingleTicket;
import pl.training.workshop.m6.s12_onemany.step3.TicketGroup;
import pl.training.workshop.shared.Money;

/** Composite pozwala zagnieżdżać grupy - potrącenie wciąż jest naliczane raz na zwrot. */
final class S12SolutionTest {
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 2, 12, 0);

    @Test
    void nestedGroupsAreRefundedAsOneRequest() {
        var imax = new SingleTicket(new TicketData(Money.of("40.00"), NOW.plusDays(3)));
        var family = new TicketGroup(List.of(
                new SingleTicket(new TicketData(Money.of("25.00"), NOW.plusDays(3))),
                new SingleTicket(new TicketData(Money.of("25.00"), NOW.plusDays(3)))));
        assertEquals(Money.of("87.00"), new RefundService().refund(new TicketGroup(List.of(imax, family)), NOW));
    }

    @SuppressWarnings("deprecation")
    @Test
    void deprecatedWrappersInStep2DelegateToTheNewContract() {
        var service = new pl.training.workshop.m6.s12_onemany.step2.RefundService();
        var ticket = new TicketData(Money.of("40.00"), NOW.plusDays(3));
        assertEquals(Money.of("37.00"), service.refund(ticket, NOW));
        assertEquals(Money.of("77.00"), service.refundAll(List.of(ticket, ticket), NOW));
    }
}
