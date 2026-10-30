package pl.training.workshop.exercises.e05_giftcard.step2

import java.time.DayOfWeek

/**
 * Krok 2: jedyne miejsce, które zna tekstową składnię reguły
 * ("format=X", "day IN MON,TUE", "premiere", prefiks "NOT ", łącznik " AND ").
 * Zamienia tekst na drzewo wyrażeń; ocena i opis są już sprawą drzewa.
 */
object VoucherRuleParser {
    private val DAY_SEPARATOR = Regex("\\s*,\\s*")

    fun parse(rule: String): VoucherRule {
        if (rule.isBlank()) {
            throw IllegalArgumentException("rule must not be empty")
        }
        return rule.split(" AND ").dropLastWhile { it.isEmpty() }
            .map { term -> term(term.trim()) }
            .reduce { result, parsed -> And(result, parsed) }
    }

    /** Jak w start: "NOT " może poprzedzać tylko pojedyncze pojęcie (podwójne NOT to nieznane pojęcie). */
    private fun term(term: String): VoucherRule {
        if (term.startsWith("NOT ")) {
            return Not(atom(term.substring(4).trim()))
        }
        return atom(term)
    }

    private fun atom(term: String): VoucherRule {
        if (term.startsWith("format=")) {
            return FormatIs(term.substring("format=".length).trim())
        }
        if (term.startsWith("day IN ")) {
            return DayIn(days(term.substring("day IN ".length)))
        }
        if (term == "premiere") {
            return IsPremiere
        }
        throw IllegalArgumentException("unknown rule term: $term")
    }

    private fun days(list: String): List<DayOfWeek> = list.trim().split(DAY_SEPARATOR).dropLastWhile { it.isEmpty() }.map { token ->
        when (token) {
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
}
