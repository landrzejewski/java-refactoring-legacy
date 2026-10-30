package pl.training.workshop.exercises.e09_cashdrawer.start

import pl.training.workshop.exercises.e09_cashdrawer.DrawerLine

/** Start: jedyne miejsce tworzenia wpisów - tłumaczy surową linię dziennika na wpis szuflady. */
object DrawerEntries {
    fun from(line: DrawerLine): DrawerEntry = when (line.kind) {
        "CASH" -> CashSale(line.reference, line.amount)
        "CARD" -> CardSale(line.reference, line.amount, line.note)
        "REFUND" -> Refund(line.reference, line.amount, line.note)
        "DEPOSIT" -> Deposit(line.amount, line.note)
        else -> throw IllegalArgumentException("unknown entry kind: ${line.kind}")
    }
}
