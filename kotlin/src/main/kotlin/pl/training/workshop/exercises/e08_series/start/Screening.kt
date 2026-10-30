package pl.training.workshop.exercises.e08_series.start

import java.time.LocalDateTime
import java.util.TreeMap

import pl.training.workshop.shared.Money

/**
 * Start: seans z konstruktorem o dziewięciu parametrach. Wywołujący przekazuje `null`
 * tam, gdzie "ma być domyślnie", a klasa część domyślnych wartości uzupełnia sama
 * (pusta mapa dopłat, cena bazowa z formatu w [describe]), część zostawia planerowi.
 * Mapa dopłat jest współdzielona z tym, kto ją przekazał.
 */
class Screening(
    val title: String,
    val format: String,
    val hall: String?,
    val start: LocalDateTime,
    val basePrice: Money?,
    surcharges: MutableMap<String, Money>?,
    val subtitles: Boolean,
    val dubbing: Boolean,
    val ageLimit: Int
) {
    val surcharges: MutableMap<String, Money> = surcharges ?: LinkedHashMap()

    fun describe(): String {
        val price = basePrice ?: defaultPrice(format)
        val hallName = hall ?: "A"
        val language = if (dubbing) "dubbing" else if (subtitles) "napisy" else "oryginal"
        return "$title $format sala $hallName $start cena $price doplaty ${TreeMap(surcharges)} $language $ageLimit+"
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
