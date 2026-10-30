package pl.training.workshop.exercises.e02_hallmap.start

import pl.training.workshop.exercises.e02_hallmap.SectionSpec

/** Start: sektor to nazwa i lista rzędów - kolejny poziom, przez który klient musi przejść pętlą. */
class Section private constructor(internal val name: String, rows: List<Row>) {
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
