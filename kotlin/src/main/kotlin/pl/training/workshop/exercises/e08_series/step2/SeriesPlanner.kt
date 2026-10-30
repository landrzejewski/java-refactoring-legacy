package pl.training.workshop.exercises.e08_series.step2

import pl.training.workshop.exercises.e08_series.SeriesRequest
import pl.training.workshop.shared.Money

/**
 * Krok 2: planer składa szablon budowniczym i ustawia tylko to, co przyszło w zamówieniu.
 * Nie zna już cen formatów, domyślnej sali ani limitu wieku; sprawdza jedynie liczbę tygodni,
 * bo to reguła serii, a nie pojedynczego seansu. Kolejne tygodnie nadal powstają z kopii szablonu.
 */
class SeriesPlanner {
    fun plan(request: SeriesRequest): List<Screening> {
        if (request.weeks < 1) {
            throw IllegalArgumentException("weeks must be positive")
        }
        val builder = Screening.builder()
            .title(request.title)
            .format(request.format)
            .start(request.firstStart)
        if (request.hall != null) {
            builder.hall(request.hall)
        }
        if (request.basePrice != null) {
            builder.basePrice(request.basePrice)
        }
        if (request.dubbing != null) {
            builder.dubbing(request.dubbing)
        }
        if (request.ageLimit != null) {
            builder.ageLimit(request.ageLimit)
        }
        val template = builder.build()

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
        val PREMIERE = Money.of("5.00")
    }
}
