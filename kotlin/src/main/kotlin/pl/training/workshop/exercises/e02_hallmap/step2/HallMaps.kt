package pl.training.workshop.exercises.e02_hallmap.step2

import pl.training.workshop.exercises.e02_hallmap.HallSpec

/**
 * Krok 2: klient nie zna już poziomów sali. Agregaty liczy drzewo, a po miejscach
 * przechodzi zwykłą pętlą for - kolejność i pomijanie luk należą do sali.
 */
class HallMaps {
    fun describe(spec: HallSpec): String {
        val hall = Hall.of(spec)

        val counts = linkedMapOf<String, Int>()
        val seats = mutableListOf<String>()
        for (seat in hall) {
            counts.merge(seat.categoryName, 1, Int::plus)
            seats += "${seat.label} ${seat.icon}"
        }

        val categories = counts.entries.joinToString(" ") { (name, count) -> "$name=$count" }
        return "Sala ${hall.name}: pojemnosc ${hall.capacity()}, doplaty ${hall.surcharges()}\n" +
            "Kategorie: $categories\n" +
            "Miejsca: ${seats.joinToString(", ")}"
    }
}
