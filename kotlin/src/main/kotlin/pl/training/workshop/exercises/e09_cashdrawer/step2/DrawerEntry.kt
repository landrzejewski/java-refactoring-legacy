package pl.training.workshop.exercises.e09_cashdrawer.step2

/** Krok 2: wpis przyjmuje odwiedzającego i sam wybiera jego metodę (double dispatch). */
interface DrawerEntry {
    fun <R> accept(visitor: DrawerEntryVisitor<R>): R
}
