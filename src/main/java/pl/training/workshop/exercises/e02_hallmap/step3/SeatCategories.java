package pl.training.workshop.exercises.e02_hallmap.step3;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: fabryka współdzielonych kategorii. Pierwsze pytanie o kod tworzy kategorię,
 * każde kolejne zwraca tę samą instancję. Nieznany kod daje ten sam błąd co w start.
 */
public final class SeatCategories {
    private static final Map<Character, SeatCategory> SHARED = new ConcurrentHashMap<>();

    private SeatCategories() {
    }

    public static SeatCategory of(char code) {
        SeatCategory category = SHARED.get(code);
        if (category == null) {
            category = SHARED.computeIfAbsent(code, SeatCategories::create);
        }
        return category;
    }

    /** Wszystkie dotąd utworzone kategorie - do podglądu, ile naprawdę istnieje instancji. */
    public static Collection<SeatCategory> known() {
        return Collections.unmodifiableCollection(SHARED.values());
    }

    private static SeatCategory create(char code) {
        return switch (code) {
            case 'N' -> new SeatCategory('N', "Normalne", Money.ZERO, "o", "Miejsce standardowe");
            case 'V' -> new SeatCategory('V', "VIP", Money.of("10.00"), "*", "Fotel skorzany z szerszym rozstawem");
            case 'W' -> new SeatCategory('W', "Wozek", Money.ZERO, "&", "Miejsce dla osoby na wozku");
            case 'D' -> new SeatCategory('D', "Dwuosobowe", Money.of("6.00"), "=", "Sofa dla dwojga");
            default -> throw new IllegalArgumentException("unknown seat category: " + code);
        };
    }
}
