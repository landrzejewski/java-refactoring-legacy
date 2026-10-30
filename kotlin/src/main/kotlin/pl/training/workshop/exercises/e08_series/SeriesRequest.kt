package pl.training.workshop.exercises.e08_series

import java.time.LocalDateTime

import pl.training.workshop.shared.Money

/**
 * Stabilny kontrakt ćwiczenia: zamówienie serii tygodniowej seansów z jednego szablonu.
 * Pola `hall`, `basePrice`, `dubbing` i `ageLimit` mogą być `null`
 * (wtedy obowiązują wartości domyślne kina). `premiereWeek` dolicza dopłatę PREMIERA tylko
 * w pierwszym tygodniu, `lastWeekHall` przenosi ostatni seans serii do innej sali.
 */
data class SeriesRequest(
    val title: String,
    val format: String,
    val hall: String?,
    val firstStart: LocalDateTime,
    val weeks: Int,
    val basePrice: Money?,
    val dubbing: Boolean?,
    val ageLimit: Int?,
    val premiereWeek: Boolean,
    val lastWeekHall: String?
)
