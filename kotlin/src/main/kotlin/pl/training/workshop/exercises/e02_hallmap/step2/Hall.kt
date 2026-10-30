package pl.training.workshop.exercises.e02_hallmap.step2

import pl.training.workshop.exercises.e02_hallmap.HallSpec

/**
 * Krok 2: sala jest kolekcją miejsc - da się po niej przejść pętlą for. Typowany dostęp
 * do sektorów zniknął, bo klient już go nie potrzebuje.
 */
class Hall private constructor(internal val name: String, sections: List<Section>) : HallGroup(sections), Iterable<Seat> {
    override fun iterator(): Iterator<Seat> = SeatIterator(this)

    companion object {
        internal fun of(spec: HallSpec): Hall {
            if (spec.sections.isEmpty()) {
                throw IllegalArgumentException("hall ${spec.name} has no sections")
            }
            val sections = spec.sections.map { Section.of(it) }
            return Hall(spec.name, sections)
        }
    }
}
