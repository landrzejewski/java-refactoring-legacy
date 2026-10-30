package pl.training.workshop.exercises.e02_hallmap.step1

import pl.training.workshop.exercises.e02_hallmap.HallSpec

/** Krok 1: sala jest węzłem drzewa - pojemność i dopłaty dziedziczy z [HallGroup]. */
class Hall private constructor(internal val name: String, sections: List<Section>) : HallGroup(sections) {
    internal val sections: List<Section> = sections.toList()

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
