package pl.training.workshop.exercises.e01_import.step1

import java.time.LocalTime
import java.time.format.DateTimeParseException

import pl.training.workshop.exercises.e01_import.ScreeningEntry
import pl.training.workshop.shared.Money

/**
 * Krok 1: wspólny szkielet importu (pętla, numeracja wierszy, pomijanie komentarzy, walidacja,
 * sortowanie) jest w jednym miejscu i jest final (w Kotlinie: metoda bez `open`). Podklasa dostarcza
 * tylko to, co zależy od formatu: rozpoznanie komentarza i rozbiór jednego wiersza na surowe pola.
 * Wybór importera po rozszerzeniu nadal jest tutaj - i nadal powtarza się u klienta.
 */
abstract class RepertoireImporter {
    fun importLines(lines: List<String>): List<ScreeningEntry> {
        val entries = mutableListOf<ScreeningEntry>()
        var number = 0
        for (raw in lines) {
            number++
            val line = raw.trim()
            if (line.isEmpty() || isComment(line)) {
                continue
            }
            entries += toEntry(number, parseLine(line, number))
        }
        return entries.sortedWith(compareBy<ScreeningEntry> { it.start }.thenBy { it.title })
    }

    protected abstract fun isComment(line: String): Boolean

    protected abstract fun parseLine(line: String, number: Int): RawScreening

    companion object {
        private val MORNING_DISCOUNT = Money.of("5.00")

        fun importFile(fileName: String, lines: List<String>): List<ScreeningEntry> {
            if (fileName.endsWith(".csv")) {
                return CsvRepertoireImporter().importLines(lines)
            } else if (fileName.endsWith(".jsonl")) {
                return JsonLinesRepertoireImporter().importLines(lines)
            }
            throw IllegalArgumentException("unsupported file: $fileName")
        }

        private fun toEntry(number: Int, raw: RawScreening): ScreeningEntry {
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
