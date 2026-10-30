package pl.training.workshop.exercises.e02_hallmap.step2

import pl.training.workshop.shared.Money

/** Krok 2: węzeł drzewa - bez zmian; listę dzieci czyta teraz tylko [SeatIterator]. */
abstract class HallGroup protected constructor(children: List<HallElement>) : HallElement {
    internal val children: List<HallElement> = children.toList()

    final override fun capacity(): Int = children.sumOf { it.capacity() }

    final override fun surcharges(): Money = children.fold(Money.ZERO) { sum, child -> sum + child.surcharges() }
}
