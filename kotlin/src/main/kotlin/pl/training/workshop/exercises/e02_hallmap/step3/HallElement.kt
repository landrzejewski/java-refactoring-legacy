package pl.training.workshop.exercises.e02_hallmap.step3

import pl.training.workshop.shared.Money

/** Krok 3: kontrakt elementu sali - bez zmian. */
interface HallElement {
    fun capacity(): Int

    fun surcharges(): Money
}
