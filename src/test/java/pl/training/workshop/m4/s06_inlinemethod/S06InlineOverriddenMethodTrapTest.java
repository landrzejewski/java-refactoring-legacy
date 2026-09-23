package pl.training.workshop.m4.s06_inlinemethod;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/**
 * Dokumentuje pułapkę: Inline Method na {@code bookingFee()} w klasie bazowej.
 * Podklasa nadal się kompiluje, @Override nadal jest poprawne - ale nikt już jej metody nie woła.
 */
final class S06InlineOverriddenMethodTrapTest {
    @Test
    void inliningAnOverriddenMethodSilentlyDropsTheOnlineFee() {
        assertEquals("42.00", new pl.training.workshop.m4.s06_inlinemethod.step2.OnlineTicketPricing()
                .total(S06EquivalenceTest.IMAX_EVENING).toPlainString());
        assertEquals("40.00", new NaiveOnlinePricing().total(S06EquivalenceTest.IMAX_EVENING).toPlainString(),
                "po wklejeniu ciała bookingFee() z bazy internet sprzedaje bez opłaty");
    }

    /** step2 po naiwnym Inline Method {@code bookingFee}: ciało z klasy bazowej wklejone do total. */
    static class NaivePricing {
        public BigDecimal total(Ticket ticket) {
            return new pl.training.workshop.m4.s06_inlinemethod.step2.TicketPricing().price(ticket)
                    .add(new BigDecimal("0.00"));
        }

        protected BigDecimal bookingFee() {
            return new BigDecimal("0.00");
        }
    }

    static final class NaiveOnlinePricing extends NaivePricing {
        @Override
        protected BigDecimal bookingFee() {
            return new BigDecimal("2.00");
        }
    }
}
