package pl.training.workshop.m6.s19_visitor.step2;

import java.math.BigDecimal;
import java.math.RoundingMode;

import pl.training.workshop.shared.Money;

/** Krok 2: operacja "VAT zawarty w cenie" jako Visitor (bilety 8%, bar 23%, voucher 0). */
public final class VatVisitor implements OrderItemVisitor<Money> {
    @Override
    public Money visitTicket(TicketItem ticket) {
        return vatOf(ticket.price(), 8);
    }

    @Override
    public Money visitSnack(SnackItem snack) {
        return vatOf(snack.price(), 23);
    }

    @Override
    public Money visitVoucher(VoucherItem voucher) {
        return Money.ZERO;
    }

    private static Money vatOf(Money gross, int rate) {
        return new Money(gross.amount().multiply(BigDecimal.valueOf(rate))
                .divide(BigDecimal.valueOf(100L + rate), 2, RoundingMode.HALF_UP));
    }
}
