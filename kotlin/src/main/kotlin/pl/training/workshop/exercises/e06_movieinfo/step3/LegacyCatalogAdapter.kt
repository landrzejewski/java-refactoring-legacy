package pl.training.workshop.exercises.e06_movieinfo.step3

import java.math.BigDecimal

import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRows
import pl.training.workshop.exercises.e06_movieinfo.MovieDetails

/** Krok 3: adapter starej bazy z tabelą kategorii wstrzykniętą przez konstruktor. */
class LegacyCatalogAdapter(
    private val rows: LegacyMovieRows,
    private val ratings: AgeRatings = AgeRatingTable.INSTANCE
) : MovieCatalog {
    override fun find(id: String): MovieDetails {
        val row = rows.find(id) ?: throw IllegalArgumentException("unknown movie: $id")
        return MovieDetails(
            row.title,
            row.year,
            row.minutes,
            BigDecimal.valueOf(row.ratingTimesTen.toLong(), 1),
            ratings.categoryFor("LEGACY", row.ageCode)
        )
    }
}
