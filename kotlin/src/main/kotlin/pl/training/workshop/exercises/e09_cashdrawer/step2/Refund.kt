package pl.training.workshop.exercises.e09_cashdrawer.step2

import pl.training.workshop.shared.Money

/** Krok 2: zwrot gotówki za bilet - pomniejsza szufladę, w eksporcie fiskalnym kwota ujemna. */
data class Refund(val reference: String, val amount: Money, val reason: String) : DrawerEntry {
    override fun <R> accept(visitor: DrawerEntryVisitor<R>): R = visitor.visitRefund(this)
}
