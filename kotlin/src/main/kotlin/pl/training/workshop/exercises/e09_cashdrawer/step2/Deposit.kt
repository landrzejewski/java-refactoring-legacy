package pl.training.workshop.exercises.e09_cashdrawer.step2

import pl.training.workshop.shared.Money

/** Krok 2: pogotowie kasowe (wpłata do szuflady) - gotówka, ale bez linii fiskalnej. */
data class Deposit(val amount: Money, val note: String) : DrawerEntry {
    override fun <R> accept(visitor: DrawerEntryVisitor<R>): R = visitor.visitDeposit(this)
}
