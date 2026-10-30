package pl.training.workshop.exercises.e01_import.start

import java.time.LocalTime
import java.time.format.DateTimeParseException

import pl.training.workshop.exercises.e01_import.ScreeningEntry
import pl.training.workshop.shared.Money

/**
 * Start: import repertuaru z CSV (`tytuł;format;sala;start`, komentarze od `#`)
 * i z JSON Lines (jeden obiekt na wiersz, komentarze od `//`). Obie metody powstały przez
 * kopiowanie: pętla po wierszach, pomijanie pustych i komentarzy, numerowanie wierszy do komunikatów,
 * wspólna walidacja i sortowanie są w dwóch egzemplarzach. Różni się tylko sposób rozbioru wiersza.
 * Gdy dystrybutor doda trzeci format (XML), powstanie trzecia kopia.
 */
class RepertoireImporter {
    fun importFile(fileName: String, lines: List<String>): List<ScreeningEntry> {
        if (fileName.endsWith(".csv")) {
            return importCsv(lines)
        } else if (fileName.endsWith(".jsonl")) {
            return importJsonLines(lines)
        }
        throw IllegalArgumentException("unsupported file: $fileName")
    }

    fun importCsv(lines: List<String>): List<ScreeningEntry> {
        val entries = mutableListOf<ScreeningEntry>()
        var number = 0
        for (raw in lines) {
            number++
            val line = raw.trim()
            if (line.isEmpty() || line.startsWith("#")) {
                continue
            }
            val fields = line.split(";")
            if (fields.size != 4) {
                throw IllegalArgumentException("line $number: expected 4 fields, got ${fields.size}")
            }
            entries += toEntry(number, fields[0].trim(), fields[1].trim(), fields[2].trim(), fields[3].trim())
        }
        return entries.sortedWith(compareBy<ScreeningEntry> { it.start }.thenBy { it.title })
    }

    fun importJsonLines(lines: List<String>): List<ScreeningEntry> {
        val entries = mutableListOf<ScreeningEntry>()
        var number = 0
        for (raw in lines) {
            number++
            val line = raw.trim()
            if (line.isEmpty() || line.startsWith("//")) {
                continue
            }
            if (!line.startsWith("{") || !line.endsWith("}")) {
                throw IllegalArgumentException("line $number: not a JSON object")
            }
            val fields = linkedMapOf<String, String>()
            for (match in JSON_FIELD.findAll(line)) {
                fields[match.groupValues[1]] = match.groupValues[2]
            }
            for (required in listOf("title", "format", "hall", "start")) {
                if (required !in fields) {
                    throw IllegalArgumentException("line $number: missing field: $required")
                }
            }
            entries += toEntry(number, fields.getValue("title"), fields.getValue("format"), fields.getValue("hall"), fields.getValue("start"))
        }
        return entries.sortedWith(compareBy<ScreeningEntry> { it.start }.thenBy { it.title })
    }

    private companion object {
        val JSON_FIELD = Regex("\"(\\w+)\"\\s*:\\s*\"([^\"]*)\"")
        val MORNING_DISCOUNT = Money.of("5.00")

        fun toEntry(number: Int, title: String, format: String, hall: String, start: String): ScreeningEntry {
            if (title.isEmpty()) {
                throw IllegalArgumentException("line $number: empty title")
            }
            val base = when (format) {
                "2D" -> Money.of("25.00")
                "3D" -> Money.of("32.00")
                "IMAX" -> Money.of("40.00")
                else -> throw IllegalArgumentException("line $number: unknown format: $format")
            }
            if (hall.isEmpty()) {
                throw IllegalArgumentException("line $number: empty hall")
            }
            val time = try {
                LocalTime.parse(start)
            } catch (exception: DateTimeParseException) {
                throw IllegalArgumentException("line $number: bad start time: $start")
            }
            val price = if (time.isBefore(LocalTime.NOON)) base - MORNING_DISCOUNT else base
            return ScreeningEntry(time, title, format, hall, price)
        }
    }
}
