package pl.training.workshop.exercises.e06_movieinfo.step1

import java.math.BigDecimal

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails
import pl.training.workshop.exercises.e06_movieinfo.OmdbLikeClient

/**
 * Krok 1: adapter internetowej bazy. Jako jedyny zna format odpowiedzi OMDb: "155 min", "8.0/10",
 * rok jako tekst, kody wieku G/PG/PG-13/R i odmowę przez Response=False. Tłumaczy nie tylko nazwy,
 * ale też jednostki i sposób zgłaszania błędu.
 */
class OmdbCatalogAdapter(private val client: OmdbLikeClient) : MovieCatalog {
    override fun find(id: String): MovieDetails {
        val raw = client.byId(id)
        if ("False" == raw["Response"]) {
            throw IllegalArgumentException("unknown movie: $id")
        }
        val table = AgeRatingTable.load()
        val rating = raw.getValue("imdbRating")
        return MovieDetails(
            raw.getValue("Title"),
            raw.getValue("Year").toInt(),
            raw.getValue("Runtime").replace(" min", "").toInt(),
            BigDecimal(rating.substring(0, rating.indexOf('/'))),
            table.categoryFor("OMDB", raw.getValue("Rated"))
        )
    }
}
