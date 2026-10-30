package pl.training.workshop.exercises.e09_cashdrawer.step2

import pl.training.workshop.shared.Money

/** Krok 2: sprzedaż biletu za gotówkę - zasila szufladę. */
data class CashSale(val reference: String, val amount: Money) : DrawerEntry {
    override fun <R> accept(visitor: DrawerEntryVisitor<R>): R = visitor.visitCashSale(this)
}
