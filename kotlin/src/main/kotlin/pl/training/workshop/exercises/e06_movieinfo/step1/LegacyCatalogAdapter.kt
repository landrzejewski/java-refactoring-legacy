package pl.training.workshop.exercises.e06_movieinfo.step1

import java.math.BigDecimal

import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRows
import pl.training.workshop.exercises.e06_movieinfo.MovieDetails

/**
 * Krok 1: adapter starej bazy. Jako jedyny wie, że ocena jest pomnożona przez 10, kod wieku to
 * tekst "0".."16", a nieznany film to `null` zamiast wyjątku.
 */
class LegacyCatalogAdapter(private val rows: LegacyMovieRows) : MovieCatalog {
    override fun find(id: String): MovieDetails {
        val row = rows.find(id) ?: throw IllegalArgumentException("unknown movie: $id")
        val table = AgeRatingTable.load()
        return MovieDetails(
            row.title,
            row.year,
            row.minutes,
            BigDecimal.valueOf(row.ratingTimesTen.toLong(), 1),
            table.categoryFor("LEGACY", row.ageCode)
        )
    }
}
