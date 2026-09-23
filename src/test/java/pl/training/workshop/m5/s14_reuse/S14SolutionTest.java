package pl.training.workshop.m5.s14_reuse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import pl.training.workshop.shared.Money;

/** Dziedziczenie dla reużycia łamie substytucję; kompozycja + rola przywraca uczciwy kontrakt. */
final class S14SolutionTest {
    @Test
    void regularAccountRedeemsFreeTicket() {
        pl.training.workshop.m5.s14_reuse.step1.LoyaltyAccount account = new pl.training.workshop.m5.s14_reuse.step1.LoyaltyAccount("anna@kino.pl");
        account.earn(Money.of("1000.00"));
        assertTrue(account.redeemFreeTicket());
        assertEquals(0, account.points());
    }

    @Test
    void beforeSplitCorporateAccountBreaksBaseContract() {
        pl.training.workshop.m5.s14_reuse.step1.LoyaltyAccount account = new pl.training.workshop.m5.s14_reuse.step1.CorporateAccount("Kino-Tech");
        account.earn(Money.of("1000.00"));
        assertThrows(UnsupportedOperationException.class, account::redeemFreeTicket,
                "pułapka: podtyp odrzuca operację, którą obiecuje nadtyp");
    }

    @Test
    void solutionCorporateAccountIsNotALoyaltyAccount() {
        assertFalse(pl.training.workshop.m5.s14_reuse.step2.LoyaltyAccount.class.isAssignableFrom(pl.training.workshop.m5.s14_reuse.step2.CorporateAccount.class));
        assertTrue(pl.training.workshop.m5.s14_reuse.step2.PointsHolder.class.isAssignableFrom(pl.training.workshop.m5.s14_reuse.step2.CorporateAccount.class));
        assertFalse(Arrays.stream(pl.training.workshop.m5.s14_reuse.step2.CorporateAccount.class.getMethods())
                .anyMatch(m -> m.getName().equals("redeemFreeTicket")));
    }
}
