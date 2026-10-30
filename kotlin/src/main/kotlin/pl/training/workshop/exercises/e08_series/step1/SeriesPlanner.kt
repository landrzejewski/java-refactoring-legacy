package pl.training.workshop.exercises.e08_series.step1

import pl.training.workshop.exercises.e08_series.SeriesRequest
import pl.training.workshop.shared.Money

/**
 * Krok 1: planer nie kopiuje już seansu pole po polu. Kolejny tydzień to `template.withStart(...)`,
 * premiera to `withSurcharge(...)` na kopii, a inna sala to `withHall(...)`.
 * Dopłata premierowa zostaje w pierwszym tygodniu (w start wyciekała do wszystkich).
 * Domyślna sala przenosi się do szablonu, bo kopia już jej nie uzupełnia.
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
            request.title, request.format,
            request.hall ?: "A",
            request.firstStart, base, surcharges, !dubbing, dubbing, ageLimit
        )

        val series = mutableListOf<Screening>()
        for (week in 0 until request.weeks) {
            var copy = template.withStart(template.start.plusWeeks(week.toLong()))
            if (week == 0 && request.premiereWeek) {
                copy = copy.withSurcharge("PREMIERA", PREMIERE)
            }
            series += copy
        }
        if (request.lastWeekHall != null) {
            series[series.lastIndex] = series.last().withHall(request.lastWeekHall)
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
