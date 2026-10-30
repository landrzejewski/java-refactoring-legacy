package pl.training.workshop.exercises.e02_hallmap.step1

import pl.training.workshop.exercises.e02_hallmap.SectionSpec

/** Krok 1: sektor jest węzłem drzewa nad rzędami. */
class Section private constructor(internal val name: String, rows: List<Row>) : HallGroup(rows) {
    internal val rows: List<Row> = rows.toList()

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
