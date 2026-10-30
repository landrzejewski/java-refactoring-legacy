package pl.training.workshop.exercises.e06_movieinfo.step3

import java.math.BigDecimal

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails
import pl.training.workshop.exercises.e06_movieinfo.OmdbLikeClient

/**
 * Krok 3: adapter dostaje tabelę kategorii przez konstruktor. Domyślna wartość parametru `ratings`
 * zachowuje dotychczasowe zachowanie (INSTANCE), test może podać inną tabelę bez globalnego stanu.
 */
class OmdbCatalogAdapter(
    private val client: OmdbLikeClient,
    private val ratings: AgeRatings = AgeRatingTable.INSTANCE
) : MovieCatalog {
    override fun find(id: String): MovieDetails {
        val raw = client.byId(id)
        if ("False" == raw["Response"]) {
            throw IllegalArgumentException("unknown movie: $id")
        }
        val rating = raw.getValue("imdbRating")
        return MovieDetails(
            raw.getValue("Title"),
            raw.getValue("Year").toInt(),
            raw.getValue("Runtime").replace(" min", "").toInt(),
            BigDecimal(rating.substring(0, rating.indexOf('/'))),
            ratings.categoryFor("OMDB", raw.getValue("Rated"))
        )
    }
}
