package pl.training.workshop.exercises.e02_hallmap.step1

import pl.training.workshop.exercises.e02_hallmap.RowSpec
import pl.training.workshop.shared.Money

/**
 * Krok 1: rząd jest węzłem drzewa nad miejscami. Luka nie jest już `null` w tablicy:
 * przejście po prostu nie tworzy dziecka, a numer miejsca nadal wynika z pozycji w układzie.
 */
class Row private constructor(internal val label: String, seats: List<Seat>) : HallGroup(seats) {
    internal val seats: List<Seat> = seats.toList()

    companion object {
        internal fun of(spec: RowSpec): Row {
            val layout = spec.layout
            val seats = mutableListOf<Seat>()
            for (i in layout.indices) {
                val code = layout[i]
                if (code == '_') {
                    continue
                }
                seats += seatFor(code, "${spec.label}${i + 1}")
            }
            if (seats.isEmpty()) {
                throw IllegalArgumentException("row ${spec.label} has no seats")
            }
            return Row(spec.label, seats)
        }

        private fun seatFor(code: Char, label: String): Seat = when (code) {
            'N' -> Seat(label, 'N', "Normalne", Money.ZERO, "o", "Miejsce standardowe")
            'V' -> Seat(label, 'V', "VIP", Money.of("10.00"), "*", "Fotel skorzany z szerszym rozstawem")
            'W' -> Seat(label, 'W', "Wozek", Money.ZERO, "&", "Miejsce dla osoby na wozku")
            'D' -> Seat(label, 'D', "Dwuosobowe", Money.of("6.00"), "=", "Sofa dla dwojga")
            else -> throw IllegalArgumentException("unknown seat category: $code")
        }
    }
}
