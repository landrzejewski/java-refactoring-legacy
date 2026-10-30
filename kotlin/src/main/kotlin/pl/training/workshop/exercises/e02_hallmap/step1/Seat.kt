package pl.training.workshop.exercises.e02_hallmap.step1

import pl.training.workshop.shared.Money

/**
 * Krok 1: miejsce jest liściem drzewa - jego pojemność to 1, a dopłata to jego własna dopłata.
 * Dane kategorii nadal są kopiowane do każdego miejsca (to osobny zapach, krok 3).
 */
class Seat internal constructor(
    internal val label: String,
    internal val code: Char,
    internal val categoryName: String,
    internal val surcharge: Money,
    internal val icon: String,
    internal val description: String,
) : HallElement {
    override fun capacity(): Int = 1

    override fun surcharges(): Money = surcharge
}
