package pl.training.workshop.exercises.e08_series.step2

import java.time.LocalDateTime
import java.util.Collections
import java.util.TreeMap

import pl.training.workshop.shared.Money

/**
 * Krok 2: seans powstaje tylko przez [Builder]. Pola wymagane to tytuł, format i start;
 * reszta ma wartości domyślne kina ustalane w jednym miejscu ([Builder.build]):
 * sala A, cena bazowa z formatu, dopłata VIP, okulary dla 3D, napisy, limit wieku 12.
 * Konstruktor jest prywatny, więc nie da się już zbudować seansu z `null`-ami,
 * a [describe] nie musi niczego uzupełniać. Kopie z kroku 1 zostają.
 */
class Screening private constructor(
    val title: String,
    val format: String,
    val hall: String,
    val start: LocalDateTime,
    val basePrice: Money,
    surcharges: Map<String, Money>,
    val subtitles: Boolean,
    val dubbing: Boolean,
    val ageLimit: Int
) {
    private val _surcharges: MutableMap<String, Money> = LinkedHashMap(surcharges)

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
        val language = if (dubbing) "dubbing" else if (subtitles) "napisy" else "oryginal"
        return "$title $format sala $hall $start cena $basePrice doplaty ${TreeMap(_surcharges)} $language $ageLimit+"
    }

    /**
     * Budowniczy: wymagane tytuł, format i start; reszta domyślna. Walidacja w [build].
     * Konstruktor jest `internal`, a nie prywatny jak w Javie: w Kotlinie klasa zewnętrzna
     * nie widzi prywatnych składowych klasy zagnieżdżonej. Drogą dla klientów jest [Screening.builder].
     */
    class Builder internal constructor() {
        private var title: String? = null
        private var format: String? = null
        private var hall: String? = null
        private var start: LocalDateTime? = null
        private var basePrice: Money? = null
        private val extraSurcharges = linkedMapOf<String, Money>()
        private var dubbing = false
        private var ageLimit: Int? = null

        fun title(title: String): Builder {
            this.title = title
            return this
        }

        fun format(format: String): Builder {
            this.format = format
            return this
        }

        fun hall(hall: String): Builder {
            this.hall = hall
            return this
        }

        fun start(start: LocalDateTime): Builder {
            this.start = start
            return this
        }

        fun basePrice(basePrice: Money): Builder {
            this.basePrice = basePrice
            return this
        }

        fun surcharge(name: String, amount: Money): Builder {
            extraSurcharges[name] = amount
            return this
        }

        fun dubbing(dubbing: Boolean): Builder {
            this.dubbing = dubbing
            return this
        }

        fun ageLimit(ageLimit: Int): Builder {
            this.ageLimit = ageLimit
            return this
        }

        fun build(): Screening {
            val title = title
            if (title == null || title.isBlank()) {
                throw IllegalArgumentException("title must not be blank")
            }
            val format = format
            if (format == null || format !in FORMATS) {
                throw IllegalArgumentException("unknown format: $format")
            }
            val start = start ?: throw IllegalArgumentException("start is required")
            val price = basePrice ?: defaultPrice(format)
            if (price < Money.ZERO) {
                throw IllegalArgumentException("base price must not be negative")
            }
            val surcharges = linkedMapOf<String, Money>()
            surcharges["VIP"] = VIP
            if (format == "3D") {
                surcharges["OKULARY"] = GLASSES_3D
            }
            surcharges.putAll(extraSurcharges)
            return Screening(
                title, format, hall ?: DEFAULT_HALL, start, price, surcharges,
                !dubbing, dubbing, ageLimit ?: DEFAULT_AGE_LIMIT
            )
        }

        private companion object {
            val VIP = Money.of("10.00")
            val GLASSES_3D = Money.of("3.00")
            const val DEFAULT_HALL = "A"
            const val DEFAULT_AGE_LIMIT = 12

            fun defaultPrice(format: String): Money = when (format) {
                "2D" -> Money.of("25.00")
                "3D" -> Money.of("32.00")
                "IMAX" -> Money.of("40.00")
                else -> throw IllegalArgumentException("unknown format: $format")
            }
        }
    }

    companion object {
        private val FORMATS = listOf("2D", "3D", "IMAX")

        fun builder(): Builder = Builder()
    }
}
