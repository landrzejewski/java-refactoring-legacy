package pl.training.workshop.exercises.e01_import.step2

import java.time.LocalTime
import java.time.format.DateTimeParseException

import pl.training.workshop.exercises.e01_import.ScreeningEntry
import pl.training.workshop.shared.Money

/**
 * Krok 2: szkielet importu bez zmian, ale krok zależny od formatu to teraz osobny obiekt (parser),
 * który tworzy metoda wytwórcza [createLineParser]. Podklasa importera mówi tylko,
 * jaki parser tworzy i jak się podpisuje; wybór importera po rozszerzeniu jest w [Importers].
 */
abstract class RepertoireImporter {
    fun importLines(lines: List<String>): List<ScreeningEntry> {
        val parser = createLineParser()
        val entries = mutableListOf<ScreeningEntry>()
        var number = 0
        for (raw in lines) {
            number++
            val line = raw.trim()
            if (line.isEmpty() || parser.isComment(line)) {
                continue
            }
            entries += toEntry(number, parser.parse(line, number))
        }
        return entries.sortedWith(compareBy<ScreeningEntry> { it.start }.thenBy { it.title })
    }

    abstract fun label(): String

    protected abstract fun createLineParser(): LineParser

    private companion object {
        val MORNING_DISCOUNT = Money.of("5.00")

        fun toEntry(number: Int, raw: RawScreening): ScreeningEntry {
            if (raw.title.isEmpty()) {
                throw IllegalArgumentException("line $number: empty title")
            }
            val base = when (raw.format) {
                "2D" -> Money.of("25.00")
                "3D" -> Money.of("32.00")
                "IMAX" -> Money.of("40.00")
                else -> throw IllegalArgumentException("line $number: unknown format: ${raw.format}")
            }
            if (raw.hall.isEmpty()) {
                throw IllegalArgumentException("line $number: empty hall")
            }
            val time = try {
                LocalTime.parse(raw.start)
            } catch (exception: DateTimeParseException) {
                throw IllegalArgumentException("line $number: bad start time: ${raw.start}")
            }
            val price = if (time.isBefore(LocalTime.NOON)) base - MORNING_DISCOUNT else base
            return ScreeningEntry(time, raw.title, raw.format, raw.hall, price)
        }
    }
}
