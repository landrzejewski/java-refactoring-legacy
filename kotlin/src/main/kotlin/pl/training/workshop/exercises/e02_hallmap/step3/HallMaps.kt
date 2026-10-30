package pl.training.workshop.exercises.e02_hallmap.step3

import pl.training.workshop.exercises.e02_hallmap.HallSpec

/** Krok 3: klient czyta nazwę i ikonę z kategorii miejsca - wynik identyczny jak w start. */
class HallMaps {
    fun describe(spec: HallSpec): String {
        val hall = Hall.of(spec)

        val counts = linkedMapOf<String, Int>()
        val seats = mutableListOf<String>()
        for (seat in hall) {
            counts.merge(seat.category.name, 1, Int::plus)
            seats += "${seat.label} ${seat.category.icon}"
        }

        val categories = counts.entries.joinToString(" ") { (name, count) -> "$name=$count" }
        return "Sala ${hall.name}: pojemnosc ${hall.capacity()}, doplaty ${hall.surcharges()}\n" +
            "Kategorie: $categories\n" +
            "Miejsca: ${seats.joinToString(", ")}"
    }
}
