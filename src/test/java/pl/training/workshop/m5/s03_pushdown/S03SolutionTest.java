package pl.training.workshop.m5.s03_pushdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import pl.training.workshop.shared.Money;

/** Push Down zawęża kontrakt bazy - i łamie stare binaria, które wołały metodę przez nadklasę. */
final class S03SolutionTest {
    @Test
    void beforePushDownStudentTicketBreaksBaseContract() {
        pl.training.workshop.m5.s03_pushdown.step2.Ticket ticket = new pl.training.workshop.m5.s03_pushdown.step2.StudentTicket(Money.of("25.00"));
        assertThrows(UnsupportedOperationException.class, ticket::upgradeToVip);
    }

    @Test
    void beforePushDownSubclassFindsMethodInSuperclass() throws Exception {
        assertEquals(pl.training.workshop.m5.s03_pushdown.step2.Ticket.class,
                pl.training.workshop.m5.s03_pushdown.step2.StandardTicket.class.getMethod("upgradeToVip").getDeclaringClass());
    }

    @Test
    void afterPushDownBaseTypeNoLongerHasTheMethod() throws Exception {
        assertThrows(NoSuchMethodException.class, () -> pl.training.workshop.m5.s03_pushdown.step3.Ticket.class.getMethod("upgradeToVip"));
        assertEquals(pl.training.workshop.m5.s03_pushdown.step3.StandardTicket.class,
                pl.training.workshop.m5.s03_pushdown.step3.StandardTicket.class.getMethod("upgradeToVip").getDeclaringClass());
        assertThrows(NoSuchFieldException.class, () -> pl.training.workshop.m5.s03_pushdown.step3.Ticket.class.getDeclaredField("vipUpgraded"));
    }
}
