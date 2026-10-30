package pl.training.workshop.exercises.e02_hallmap.step3

import pl.training.workshop.shared.Money

/** Krok 3: węzeł drzewa - bez zmian. */
abstract class HallGroup protected constructor(children: List<HallElement>) : HallElement {
    internal val children: List<HallElement> = children.toList()

    final override fun capacity(): Int = children.sumOf { it.capacity() }

    final override fun surcharges(): Money = children.fold(Money.ZERO) { sum, child -> sum + child.surcharges() }
}
