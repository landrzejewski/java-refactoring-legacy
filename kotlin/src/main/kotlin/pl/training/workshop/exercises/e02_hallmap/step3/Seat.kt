package pl.training.workshop.exercises.e02_hallmap.step3

import pl.training.workshop.shared.Money

/**
 * Krok 3: miejsce to tylko etykieta (stan własny) i odwołanie do współdzielonej kategorii
 * (stan wspólny). 400 foteli VIP wskazuje na jeden obiekt kategorii.
 */
class Seat internal constructor(val label: String, val category: SeatCategory) : HallElement {
    override fun capacity(): Int = 1

    override fun surcharges(): Money = category.surcharge
}
