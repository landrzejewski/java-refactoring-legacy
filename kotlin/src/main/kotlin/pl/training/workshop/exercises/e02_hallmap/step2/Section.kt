package pl.training.workshop.exercises.e02_hallmap.step2

import pl.training.workshop.exercises.e02_hallmap.SectionSpec

/** Krok 2: sektor - węzeł nad rzędami; typowany dostęp do rzędów nie jest już nikomu potrzebny. */
class Section private constructor(internal val name: String, rows: List<Row>) : HallGroup(rows) {
    companion object {
        internal fun of(spec: SectionSpec): Section {
            if (spec.rows.isEmpty()) {
                throw IllegalArgumentException("section ${spec.name} has no rows")
            }
            val rows = spec.rows.map { Row.of(it) }
            return Section(spec.name, rows)
        }
    }
}
