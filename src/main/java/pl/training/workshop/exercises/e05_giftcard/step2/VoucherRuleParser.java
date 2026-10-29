package pl.training.workshop.exercises.e05_giftcard.step2;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

/**
 * Krok 2: jedyne miejsce, które zna tekstową składnię reguły
 * ("format=X", "day IN MON,TUE", "premiere", prefiks "NOT ", łącznik " AND ").
 * Zamienia tekst na drzewo wyrażeń; ocena i opis są już sprawą drzewa.
 */
public final class VoucherRuleParser {
    private VoucherRuleParser() {
    }

    public static VoucherRule parse(String rule) {
        if (rule == null || rule.isBlank()) {
            throw new IllegalArgumentException("rule must not be empty");
        }
        VoucherRule result = null;
        for (String term : rule.split(" AND ")) {
            VoucherRule parsed = term(term.trim());
            result = result == null ? parsed : new And(result, parsed);
        }
        return result;
    }

    /** Jak w start: "NOT " może poprzedzać tylko pojedyncze pojęcie (podwójne NOT to nieznane pojęcie). */
    private static VoucherRule term(String term) {
        if (term.startsWith("NOT ")) {
            return new Not(atom(term.substring(4).trim()));
        }
        return atom(term);
    }

    private static VoucherRule atom(String term) {
        if (term.startsWith("format=")) {
            return new FormatIs(term.substring("format=".length()).trim());
        }
        if (term.startsWith("day IN ")) {
            return new DayIn(days(term.substring("day IN ".length())));
        }
        if (term.equals("premiere")) {
            return new IsPremiere();
        }
        throw new IllegalArgumentException("unknown rule term: " + term);
    }

    private static List<DayOfWeek> days(String list) {
        List<DayOfWeek> days = new ArrayList<>();
        for (String token : list.trim().split("\\s*,\\s*")) {
            days.add(switch (token) {
                case "MON" -> DayOfWeek.MONDAY;
                case "TUE" -> DayOfWeek.TUESDAY;
                case "WED" -> DayOfWeek.WEDNESDAY;
                case "THU" -> DayOfWeek.THURSDAY;
                case "FRI" -> DayOfWeek.FRIDAY;
                case "SAT" -> DayOfWeek.SATURDAY;
                case "SUN" -> DayOfWeek.SUNDAY;
                default -> throw new IllegalArgumentException("unknown day: " + token);
            });
        }
        return days;
    }
}
