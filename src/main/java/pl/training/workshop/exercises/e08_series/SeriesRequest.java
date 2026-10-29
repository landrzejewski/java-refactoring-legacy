package pl.training.workshop.exercises.e08_series;

import java.time.LocalDateTime;

import pl.training.workshop.shared.Money;

/**
 * Stabilny kontrakt ćwiczenia: zamówienie serii tygodniowej seansów z jednego szablonu.
 * Pola {@code hall}, {@code basePrice}, {@code dubbing} i {@code ageLimit} mogą być {@code null}
 * (wtedy obowiązują wartości domyślne kina). {@code premiereWeek} dolicza dopłatę PREMIERA tylko
 * w pierwszym tygodniu, {@code lastWeekHall} przenosi ostatni seans serii do innej sali.
 */
public record SeriesRequest(
        String title,
        String format,
        String hall,
        LocalDateTime firstStart,
        int weeks,
        Money basePrice,
        Boolean dubbing,
        Integer ageLimit,
        boolean premiereWeek,
        String lastWeekHall) {
}
