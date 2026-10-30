package pl.training.workshop.exercises.e02_hallmap.step3

import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

import pl.training.workshop.shared.Money

/**
 * Krok 3: fabryka współdzielonych kategorii. Pierwsze pytanie o kod tworzy kategorię,
 * każde kolejne zwraca tę samą instancję. Nieznany kod daje ten sam błąd co w start.
 */
object SeatCategories {
    private val SHARED = ConcurrentHashMap<Char, SeatCategory>()

    fun of(code: Char): SeatCategory = SHARED[code] ?: SHARED.computeIfAbsent(code, ::create)

    /** Wszystkie dotąd utworzone kategorie - do podglądu, ile naprawdę istnieje instancji. */
    fun known(): Collection<SeatCategory> = Collections.unmodifiableCollection(SHARED.values)

    private fun create(code: Char): SeatCategory = when (code) {
        'N' -> SeatCategory('N', "Normalne", Money.ZERO, "o", "Miejsce standardowe")
        'V' -> SeatCategory('V', "VIP", Money.of("10.00"), "*", "Fotel skorzany z szerszym rozstawem")
        'W' -> SeatCategory('W', "Wozek", Money.ZERO, "&", "Miejsce dla osoby na wozku")
        'D' -> SeatCategory('D', "Dwuosobowe", Money.of("6.00"), "=", "Sofa dla dwojga")
        else -> throw IllegalArgumentException("unknown seat category: $code")
    }
}
