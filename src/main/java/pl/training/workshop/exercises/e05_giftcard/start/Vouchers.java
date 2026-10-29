package pl.training.workshop.exercises.e05_giftcard.start;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import pl.training.workshop.exercises.e05_giftcard.ScreeningFacts;

/**
 * Start: reguła ważności vouchera jest tekstem z konfiguracji, np.
 * "format=3D AND day IN MON,TUE AND NOT premiere". Tekst jest rozbierany dwa razy, osobno
 * w applies (czy voucher obowiązuje) i w describe (opis dla kasjera), więc każde nowe pojęcie
 * w regule (np. godzina seansu) trzeba dopisać w obu metodach, a oba rozbiory muszą się zgadzać.
 */
public final class Vouchers {
    private Vouchers() {
    }

    public static boolean applies(String rule, ScreeningFacts facts) {
        if (rule == null || rule.isBlank()) {
            throw new IllegalArgumentException("rule must not be empty");
        }
        boolean result = true;
        for (String term : rule.split(" AND ")) {
            String trimmed = term.trim();
            boolean negated = trimmed.startsWith("NOT ");
            String body = negated ? trimmed.substring(4).trim() : trimmed;
            boolean value;
            if (body.startsWith("format=")) {
                value = facts.format().equals(body.substring("format=".length()).trim());
            } else if (body.startsWith("day IN ")) {
                value = days(body.substring("day IN ".length())).contains(facts.day());
            } else if (body.equals("premiere")) {
                value = facts.premiere();
            } else {
                throw new IllegalArgumentException("unknown rule term: " + body);
            }
            result &= negated != value;
        }
        return result;
    }

    public static String describe(String rule) {
        if (rule == null || rule.isBlank()) {
            throw new IllegalArgumentException("rule must not be empty");
        }
        List<String> parts = new ArrayList<>();
        for (String term : rule.split(" AND ")) {
            String trimmed = term.trim();
            boolean negated = trimmed.startsWith("NOT ");
            String body = negated ? trimmed.substring(4).trim() : trimmed;
            String text;
            if (body.startsWith("format=")) {
                text = "format " + body.substring("format=".length()).trim();
            } else if (body.startsWith("day IN ")) {
                String list = body.substring("day IN ".length());
                days(list);
                text = "dzień: " + String.join(", ", list.trim().split("\\s*,\\s*"));
            } else if (body.equals("premiere")) {
                text = "premiera";
            } else {
                throw new IllegalArgumentException("unknown rule term: " + body);
            }
            parts.add(negated ? "nie " + text : text);
        }
        return String.join(" oraz ", parts);
    }

    private static Set<DayOfWeek> days(String list) {
        Set<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
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
