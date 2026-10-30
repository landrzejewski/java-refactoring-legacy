package pl.training.workshop.exercises.e09_cashdrawer.step2

/** Krok 2: operacja "linia audytu" - czytelny opis każdego wpisu. */
class AuditLineVisitor : DrawerEntryVisitor<String> {
    override fun visitCashSale(sale: CashSale): String = "CASH ${sale.reference} ${sale.amount}"

    override fun visitCardSale(sale: CardSale): String = "CARD ${sale.reference} ${sale.amount} karta ****${sale.cardLast4}"

    override fun visitRefund(refund: Refund): String = "REFUND ${refund.reference} -${refund.amount} powod: ${refund.reason}"

    override fun visitDeposit(deposit: Deposit): String = "DEPOSIT ${deposit.amount} ${deposit.note}"
}
