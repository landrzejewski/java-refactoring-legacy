package pl.training.workshop.exercises.e08_series.step1

import java.time.LocalDateTime
import java.util.Collections
import java.util.TreeMap

import pl.training.workshop.shared.Money

/**
 * Krok 1: seans sam umie się skopiować. [copy] robi głęboką kopię mapy dopłat,
 * a [withStart], [withHall] i [withSurcharge] to "kopia z jedną zmianą".
 * Mapa dopłat nie jest już współdzielona: konstruktor ją kopiuje, a getter zwraca widok tylko do odczytu.
 * Konstruktor o dziewięciu parametrach i rozsiane wartości domyślne zostają do kroku 2.
 */
class Screening(
    val title: String,
    val format: String,
    val hall: String?,
    val start: LocalDateTime,
    val basePrice: Money?,
    surcharges: Map<String, Money>?,
    val subtitles: Boolean,
    val dubbing: Boolean,
    val ageLimit: Int
) {
    private val _surcharges: MutableMap<String, Money> = if (surcharges != null) LinkedHashMap(surcharges) else LinkedHashMap()

    val surcharges: Map<String, Money>
        get() = Collections.unmodifiableMap(_surcharges)

    /** Pełna, niezależna kopia seansu (prototyp). */
    fun copy(): Screening = Screening(title, format, hall, start, basePrice, _surcharges, subtitles, dubbing, ageLimit)

    fun withStart(newStart: LocalDateTime): Screening =
        Screening(title, format, hall, newStart, basePrice, _surcharges, subtitles, dubbing, ageLimit)

    fun withHall(newHall: String): Screening =
        Screening(title, format, newHall, start, basePrice, _surcharges, subtitles, dubbing, ageLimit)

    fun withSurcharge(name: String, amount: Money): Screening {
        val extended = LinkedHashMap(_surcharges)
        extended[name] = amount
        return Screening(title, format, hall, start, basePrice, extended, subtitles, dubbing, ageLimit)
    }

    fun describe(): String {
        val price = basePrice ?: defaultPrice(format)
        val hallName = hall ?: "A"
        val language = if (dubbing) "dubbing" else if (subtitles) "napisy" else "oryginal"
        return "$title $format sala $hallName $start cena $price doplaty ${TreeMap(_surcharges)} $language $ageLimit+"
    }

    private companion object {
        fun defaultPrice(format: String): Money = when (format) {
            "2D" -> Money.of("25.00")
            "3D" -> Money.of("32.00")
            "IMAX" -> Money.of("40.00")
            else -> throw IllegalArgumentException("unknown format: $format")
        }
    }
}
