package pl.training.workshop.exercises.e05_giftcard

import java.time.DayOfWeek

/**
 * Stabilny kontrakt sceny: fakty o seansie, do których odwołuje się reguła vouchera
 * (format seansu, dzień tygodnia, czy to premiera).
 */
data class ScreeningFacts(val format: String, val day: DayOfWeek, val premiere: Boolean)
