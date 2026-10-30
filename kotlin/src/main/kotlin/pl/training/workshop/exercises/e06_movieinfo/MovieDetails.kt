package pl.training.workshop.exercises.e06_movieinfo

import java.math.BigDecimal

/** Stabilny kontrakt sceny: dane filmu w języku kina (minuty, ocena 0-10, kategoria wiekowa kina). */
data class MovieDetails(val title: String, val year: Int, val runtimeMinutes: Int, val rating: BigDecimal, val ageCategory: String) {
    fun describe(): String =
        "$title ($year), $runtimeMinutes min, ocena ${rating.toPlainString()}/10, kategoria $ageCategory"
}
