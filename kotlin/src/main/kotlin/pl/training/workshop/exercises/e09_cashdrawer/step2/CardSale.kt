package pl.training.workshop.exercises.e09_cashdrawer.step2

import pl.training.workshop.shared.Money

/** Krok 2: sprzedaż biletu kartą - nie dotyka gotówki, ale trafia do eksportu fiskalnego. */
data class CardSale(val reference: String, val amount: Money, val cardLast4: String) : DrawerEntry {
    override fun <R> accept(visitor: DrawerEntryVisitor<R>): R = visitor.visitCardSale(this)
}
