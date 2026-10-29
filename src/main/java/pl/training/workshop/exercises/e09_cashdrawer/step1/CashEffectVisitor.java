package pl.training.workshop.exercises.e09_cashdrawer.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: operacja "wpływ wpisu na gotówkę w szufladzie" wyjęta z łańcucha instanceof. */
public final class CashEffectVisitor implements DrawerEntryVisitor<Money> {
    @Override
    public Money visitCashSale(CashSale sale) {
        return sale.amount();
    }

    @Override
    public Money visitCardSale(CardSale sale) {
        return Money.ZERO;
    }

    @Override
    public Money visitRefund(Refund refund) {
        return Money.ZERO.minus(refund.amount());
    }

    @Override
    public Money visitDeposit(Deposit deposit) {
        return deposit.amount();
    }
}
