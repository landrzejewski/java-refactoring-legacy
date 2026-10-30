package pl.training.workshop.exercises.e02_hallmap.start

import pl.training.workshop.exercises.e02_hallmap.HallSpec
import pl.training.workshop.shared.Money

/**
 * Start: klient mapy sali. Cztery razy wykonuje tę samą wędrówkę sala, sektor, rząd, miejsce
 * i za każdym razem sam pomija luki. Nowy poziom (balkon z własnymi sektorami) albo nowe
 * pytanie o salę to kolejna kopia potrójnej pętli.
 */
class HallMaps {
    fun describe(spec: HallSpec): String {
        val hall = Hall.of(spec)

        var capacity = 0
        for (section in hall.sections) {
            for (row in section.rows) {
                for (seat in row.places) {
                    if (seat == null) {
                        continue
                    }
                    capacity++
                }
            }
        }

        var surcharges = Money.ZERO
        for (section in hall.sections) {
            for (row in section.rows) {
                for (seat in row.places) {
                    if (seat == null) {
                        continue
                    }
                    surcharges += seat.surcharge
                }
            }
        }

        val counts = linkedMapOf<String, Int>()
        for (section in hall.sections) {
            for (row in section.rows) {
                for (seat in row.places) {
                    if (seat == null) {
                        continue
                    }
                    counts.merge(seat.categoryName, 1, Int::plus)
                }
            }
        }

        val seats = mutableListOf<String>()
        for (section in hall.sections) {
            for (row in section.rows) {
                for (seat in row.places) {
                    if (seat == null) {
                        continue
                    }
                    seats += "${seat.label} ${seat.icon}"
                }
            }
        }

        val categories = counts.entries.joinToString(" ") { (name, count) -> "$name=$count" }
        return "Sala ${hall.name}: pojemnosc $capacity, doplaty $surcharges\n" +
            "Kategorie: $categories\n" +
            "Miejsca: ${seats.joinToString(", ")}"
    }
}
