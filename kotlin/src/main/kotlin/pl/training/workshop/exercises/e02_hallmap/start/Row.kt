package pl.training.workshop.exercises.e02_hallmap.start

import pl.training.workshop.exercises.e02_hallmap.RowSpec
import pl.training.workshop.shared.Money

/**
 * Start: rząd trzyma tablicę miejsc, w której przejście (luka) to `null`. Każdy, kto
 * przechodzi po rzędzie, musi o tym pamiętać. Dane kategorii (nazwa, dopłata, ikona, opis)
 * są kopiowane do każdego miejsca z osobna - w sali IMAX na 400 miejsc to 400 kopii.
 */
class Row private constructor(
    internal val label: String,
    /** Tablica z lukami: `null` oznacza przejście. */
    internal val places: Array<Seat?>,
) {
    companion object {
        internal fun of(spec: RowSpec): Row {
            val layout = spec.layout
            val places = arrayOfNulls<Seat>(layout.length)
            var seats = 0
            for (i in layout.indices) {
                val code = layout[i]
                if (code == '_') {
                    continue
                }
                places[i] = seatFor(code, "${spec.label}${i + 1}")
                seats++
            }
            if (seats == 0) {
                throw IllegalArgumentException("row ${spec.label} has no seats")
            }
            return Row(spec.label, places)
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
