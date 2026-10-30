package pl.training.workshop.exercises.e09_cashdrawer.start

import pl.training.workshop.shared.Money

/** Start: zwrot gotówki za bilet - pomniejsza szufladę, w eksporcie fiskalnym kwota ujemna. */
data class Refund(val reference: String, val amount: Money, val reason: String) : DrawerEntry
