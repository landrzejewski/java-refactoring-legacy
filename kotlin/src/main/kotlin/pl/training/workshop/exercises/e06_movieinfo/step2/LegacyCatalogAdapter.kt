package pl.training.workshop.exercises.e06_movieinfo.step2

import java.math.BigDecimal

import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRows
import pl.training.workshop.exercises.e06_movieinfo.MovieDetails

/** Krok 2: adapter starej bazy bez zmian. */
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
