package pl.training.workshop.exercises.e06_movieinfo

import java.io.IOException
import java.io.UncheckedIOException

/**
 * Infrastruktura (nie zmieniamy): plik z mapowaniem obcych kodów wieku na kategorie kina,
 * czytany z classpath. Odczyt jest kosztowny (dysk, parsowanie), więc go liczymy - tak jak
 * w produkcji liczy się czas i operacje wejścia-wyjścia.
 */
object AgeRatingResource {
    private const val PATH = "/exercises/e06_movieinfo/age-ratings.csv"

    private var reads = 0

    fun readLines(): List<String> {
        reads++
        try {
            val input = AgeRatingResource::class.java.getResourceAsStream(PATH)
                ?: throw IllegalStateException("missing resource $PATH")
            return input.bufferedReader(Charsets.UTF_8).use { it.readLines() }
        } catch (exception: IOException) {
            throw UncheckedIOException(exception)
        }
    }

    /** Ile razy plik został wczytany od startu JVM. */
    fun reads(): Int = reads
}
