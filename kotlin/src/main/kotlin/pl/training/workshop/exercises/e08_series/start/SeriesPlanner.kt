package pl.training.workshop.exercises.e08_series.start

import pl.training.workshop.exercises.e08_series.SeriesRequest
import pl.training.workshop.shared.Money

/**
 * Start: planer serii tygodniowej. Szablon powstaje dziewięcioargumentowym konstruktorem,
 * a każdy kolejny tydzień i przeniesienie do innej sali to ręczna kopia pole po polu.
 * Kopia dzieli mapę dopłat z szablonem, więc dopłata premierowa dopisana do pierwszego
 * tygodnia "wycieka" do wszystkich kolejnych. Wartości domyślne (sala A, cena z formatu,
 * limit wieku 12) są rozsiane po planerze, kopii i samym seansie.
 */
class SeriesPlanner {
    fun plan(request: SeriesRequest): List<Screening> {
        if (request.weeks < 1) {
            throw IllegalArgumentException("weeks must be positive")
        }
        if (request.title.isBlank()) {
            throw IllegalArgumentException("title must not be blank")
        }
        if (request.format !in listOf("2D", "3D", "IMAX")) {
            throw IllegalArgumentException("unknown format: ${request.format}")
        }
        val base = request.basePrice ?: priceFor(request.format)
        if (base < Money.ZERO) {
            throw IllegalArgumentException("base price must not be negative")
        }
        val surcharges = linkedMapOf<String, Money>()
        surcharges["VIP"] = VIP
        if (request.format == "3D") {
            surcharges["OKULARY"] = GLASSES_3D
        }
        val dubbing = request.dubbing == true
        val ageLimit = request.ageLimit ?: 12
        val template = Screening(
            request.title, request.format, request.hall,
            request.firstStart, base, surcharges, !dubbing, dubbing, ageLimit
        )

        val series = mutableListOf<Screening>()
        for (week in 0 until request.weeks) {
            val copy = Screening(
                template.title, template.format,
                template.hall ?: "A",
                template.start.plusWeeks(week.toLong()), template.basePrice, template.surcharges,
                template.subtitles, template.dubbing, template.ageLimit
            )
            if (week == 0 && request.premiereWeek) {
                copy.surcharges["PREMIERA"] = PREMIERE
            }
            series += copy
        }
        if (request.lastWeekHall != null) {
            val last = series.last()
            series[series.lastIndex] = Screening(
                last.title, last.format, request.lastWeekHall,
                last.start, last.basePrice, last.surcharges, last.subtitles, last.dubbing,
                last.ageLimit
            )
        }
        return series
    }

    private companion object {
        val VIP = Money.of("10.00")
        val GLASSES_3D = Money.of("3.00")
        val PREMIERE = Money.of("5.00")

        fun priceFor(format: String): Money = when (format) {
            "2D" -> Money.of("25.00")
            "3D" -> Money.of("32.00")
            "IMAX" -> Money.of("40.00")
            else -> throw IllegalArgumentException("unknown format: $format")
        }
    }
}
