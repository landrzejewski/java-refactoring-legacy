package pl.training.workshop.exercises.e02_hallmap.step3

import pl.training.workshop.exercises.e02_hallmap.RowSpec

/**
 * Krok 3: rząd nie zna już danych kategorii - prosi o nią [SeatCategories] i dokłada
 * tylko to, co jest własne dla miejsca: etykietę z numerem.
 */
class Row private constructor(internal val label: String, seats: List<Seat>) : HallGroup(seats) {
    companion object {
        internal fun of(spec: RowSpec): Row {
            val layout = spec.layout
            val seats = mutableListOf<Seat>()
            for (i in layout.indices) {
                val code = layout[i]
                if (code == '_') {
                    continue
                }
                seats += Seat("${spec.label}${i + 1}", SeatCategories.of(code))
            }
            if (seats.isEmpty()) {
                throw IllegalArgumentException("row ${spec.label} has no seats")
            }
            return Row(spec.label, seats)
        }
    }
}
