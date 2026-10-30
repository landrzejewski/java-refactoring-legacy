package pl.training.workshop.exercises.e09_cashdrawer.step1

/** Krok 1: wpis przyjmuje odwiedzającego i sam wybiera jego metodę (double dispatch). */
interface DrawerEntry {
    fun <R> accept(visitor: DrawerEntryVisitor<R>): R
}
