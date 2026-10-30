package pl.training.workshop.exercises.e02_hallmap.step3

import pl.training.workshop.exercises.e02_hallmap.HallSpec

/** Krok 3: sala - korzeń drzewa i kolekcja miejsc; publiczna, bo buduje ją też test rozwiązania. */
class Hall private constructor(val name: String, sections: List<Section>) : HallGroup(sections), Iterable<Seat> {
    override fun iterator(): Iterator<Seat> = SeatIterator(this)

    companion object {
        fun of(spec: HallSpec): Hall {
            if (spec.sections.isEmpty()) {
                throw IllegalArgumentException("hall ${spec.name} has no sections")
            }
            val sections = spec.sections.map { Section.of(it) }
            return Hall(spec.name, sections)
        }
    }
}
