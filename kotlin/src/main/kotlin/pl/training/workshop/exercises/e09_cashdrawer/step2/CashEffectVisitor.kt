package pl.training.workshop.exercises.e09_cashdrawer.step2

import pl.training.workshop.shared.Money

/** Krok 2: operacja "wpływ wpisu na gotówkę w szufladzie" wyjęta z łańcucha `is`. */
class CashEffectVisitor : DrawerEntryVisitor<Money> {
    override fun visitCashSale(sale: CashSale): Money = sale.amount

    override fun visitCardSale(sale: CardSale): Money = Money.ZERO

    override fun visitRefund(refund: Refund): Money = Money.ZERO - refund.amount

    override fun visitDeposit(deposit: Deposit): Money = deposit.amount
}
