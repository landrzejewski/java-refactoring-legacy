package pl.training.workshop.exercises.e09_cashdrawer.start

import pl.training.workshop.shared.Money

/** Start: sprzedaż biletu za gotówkę - zasila szufladę. */
data class CashSale(val reference: String, val amount: Money) : DrawerEntry
