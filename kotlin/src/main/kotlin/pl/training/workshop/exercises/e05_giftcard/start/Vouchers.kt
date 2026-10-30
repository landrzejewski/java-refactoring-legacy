package pl.training.workshop.exercises.e05_giftcard.start

import java.time.DayOfWeek
import java.util.EnumSet

import pl.training.workshop.exercises.e05_giftcard.ScreeningFacts

/**
 * Start: reguła ważności vouchera jest tekstem z konfiguracji, np.
 * "format=3D AND day IN MON,TUE AND NOT premiere". Tekst jest rozbierany dwa razy, osobno
 * w applies (czy voucher obowiązuje) i w describe (opis dla kasjera), więc każde nowe pojęcie
 * w regule (np. godzina seansu) trzeba dopisać w obu metodach, a oba rozbiory muszą się zgadzać.
 */
object Vouchers {
    private val DAY_SEPARATOR = Regex("\\s*,\\s*")

    fun applies(rule: String, facts: ScreeningFacts): Boolean {
        if (rule.isBlank()) {
            throw IllegalArgumentException("rule must not be empty")
        }
        var result = true
        for (term in rule.split(" AND ").dropLastWhile { it.isEmpty() }) {
            val trimmed = term.trim()
            val negated = trimmed.startsWith("NOT ")
            val body = if (negated) trimmed.substring(4).trim() else trimmed
            val value = if (body.startsWith("format=")) {
                facts.format == body.substring("format=".length).trim()
            } else if (body.startsWith("day IN ")) {
                facts.day in days(body.substring("day IN ".length))
            } else if (body == "premiere") {
                facts.premiere
            } else {
                throw IllegalArgumentException("unknown rule term: $body")
            }
            result = result and (negated != value)
        }
        return result
    }

    fun describe(rule: String): String {
        if (rule.isBlank()) {
            throw IllegalArgumentException("rule must not be empty")
        }
        val parts = mutableListOf<String>()
        for (term in rule.split(" AND ").dropLastWhile { it.isEmpty() }) {
            val trimmed = term.trim()
            val negated = trimmed.startsWith("NOT ")
            val body = if (negated) trimmed.substring(4).trim() else trimmed
            val text = if (body.startsWith("format=")) {
                "format " + body.substring("format=".length).trim()
            } else if (body.startsWith("day IN ")) {
                val list = body.substring("day IN ".length)
                days(list)
                "dzień: " + list.trim().split(DAY_SEPARATOR).dropLastWhile { it.isEmpty() }.joinToString(", ")
            } else if (body == "premiere") {
                "premiera"
            } else {
                throw IllegalArgumentException("unknown rule term: $body")
            }
            parts += if (negated) "nie $text" else text
        }
        return parts.joinToString(" oraz ")
    }

    private fun days(list: String): Set<DayOfWeek> {
        val days = EnumSet.noneOf(DayOfWeek::class.java)
        for (token in list.trim().split(DAY_SEPARATOR).dropLastWhile { it.isEmpty() }) {
            days += when (token) {
                "MON" -> DayOfWeek.MONDAY
                "TUE" -> DayOfWeek.TUESDAY
                "WED" -> DayOfWeek.WEDNESDAY
                "THU" -> DayOfWeek.THURSDAY
                "FRI" -> DayOfWeek.FRIDAY
                "SAT" -> DayOfWeek.SATURDAY
                "SUN" -> DayOfWeek.SUNDAY
                else -> throw IllegalArgumentException("unknown day: $token")
            }
        }
        return days
    }
}
