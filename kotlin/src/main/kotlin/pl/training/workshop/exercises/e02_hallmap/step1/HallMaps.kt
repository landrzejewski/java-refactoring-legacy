package pl.training.workshop.exercises.e02_hallmap.step1

import pl.training.workshop.exercises.e02_hallmap.HallSpec

/**
 * Krok 1: pojemność i dopłaty liczy drzewo. Zliczanie kategorii i lista miejsc nadal
 * wędrują po strukturze ręcznie - klient wciąż zna poziomy sala, sektor, rząd.
 */
class HallMaps {
    fun describe(spec: HallSpec): String {
        val hall = Hall.of(spec)

        val counts = linkedMapOf<String, Int>()
        for (section in hall.sections) {
            for (row in section.rows) {
                for (seat in row.seats) {
                    counts.merge(seat.categoryName, 1, Int::plus)
                }
            }
        }

        val seats = mutableListOf<String>()
        for (section in hall.sections) {
            for (row in section.rows) {
                for (seat in row.seats) {
                    seats += "${seat.label} ${seat.icon}"
                }
            }
        }

        val categories = counts.entries.joinToString(" ") { (name, count) -> "$name=$count" }
        return "Sala ${hall.name}: pojemnosc ${hall.capacity()}, doplaty ${hall.surcharges()}\n" +
            "Kategorie: $categories\n" +
            "Miejsca: ${seats.joinToString(", ")}"
    }
}
