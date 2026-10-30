package pl.training.workshop.exercises.e02_hallmap.step1

import pl.training.workshop.shared.Money

/**
 * Krok 1: węzeł drzewa sali. Sumuje odpowiedzi dzieci, nie wiedząc, czy dziecko to rząd,
 * sektor czy miejsce. Lista dzieci jest ustalana raz i niemutowalna (wariant bezpieczny).
 */
abstract class HallGroup protected constructor(children: List<HallElement>) : HallElement {
    internal val children: List<HallElement> = children.toList()

    final override fun capacity(): Int = children.sumOf { it.capacity() }

    final override fun surcharges(): Money = children.fold(Money.ZERO) { sum, child -> sum + child.surcharges() }
}
