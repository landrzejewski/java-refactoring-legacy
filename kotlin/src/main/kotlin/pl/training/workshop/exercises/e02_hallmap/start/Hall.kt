package pl.training.workshop.exercises.e02_hallmap.start

import pl.training.workshop.exercises.e02_hallmap.HallSpec

/** Start: sala to nazwa i lista sektorów. Sama nic nie liczy - liczy klient, który zna cały układ. */
class Hall private constructor(internal val name: String, sections: List<Section>) {
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
