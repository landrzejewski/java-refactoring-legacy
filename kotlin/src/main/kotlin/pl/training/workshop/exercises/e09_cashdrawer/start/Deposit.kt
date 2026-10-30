package pl.training.workshop.exercises.e09_cashdrawer.start

import pl.training.workshop.shared.Money

/** Start: pogotowie kasowe (wpłata do szuflady) - gotówka, ale bez linii fiskalnej. */
data class Deposit(val amount: Money, val note: String) : DrawerEntry
