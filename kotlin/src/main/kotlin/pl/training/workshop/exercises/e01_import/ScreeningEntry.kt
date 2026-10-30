package pl.training.workshop.exercises.e01_import

import java.time.LocalTime

import pl.training.workshop.shared.Money

/** Pozycja zaimportowanego repertuaru: seans z wyliczoną ceną bazową biletu normalnego. */
data class ScreeningEntry(val start: LocalTime, val title: String, val format: String, val hall: String, val price: Money) {
    override fun toString(): String = "$start $title ($format, sala $hall) $price"
}
