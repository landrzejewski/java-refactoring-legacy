package pl.training.workshop.m6.s19_visitor.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: operacja "kwota do zapłaty" jako Visitor. */
public final class AmountVisitor implements OrderItemVisitor<Money> {
    @Override
    public Money visitTicket(TicketItem ticket) {
        return ticket.price();
    }

    @Override
    public Money visitSnack(SnackItem snack) {
        return snack.price();
    }

    @Override
    public Money visitVoucher(VoucherItem voucher) {
        return Money.ZERO.minus(voucher.value());
    }
}
