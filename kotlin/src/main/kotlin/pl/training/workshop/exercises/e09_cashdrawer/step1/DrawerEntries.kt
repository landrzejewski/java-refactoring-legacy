package pl.training.workshop.exercises.e09_cashdrawer.step1

import pl.training.workshop.exercises.e09_cashdrawer.DrawerLine

/** Krok 1: jedyne miejsce tworzenia wpisów - bez zmian. */
object DrawerEntries {
    fun from(line: DrawerLine): DrawerEntry = when (line.kind) {
        "CASH" -> CashSale(line.reference, line.amount)
        "CARD" -> CardSale(line.reference, line.amount, line.note)
        "REFUND" -> Refund(line.reference, line.amount, line.note)
        "DEPOSIT" -> Deposit(line.amount, line.note)
        else -> throw IllegalArgumentException("unknown entry kind: ${line.kind}")
    }
}
