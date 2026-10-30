package pl.training.workshop.exercises.e06_movieinfo.start

import java.math.BigDecimal

import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRows
import pl.training.workshop.exercises.e06_movieinfo.MovieDetails
import pl.training.workshop.exercises.e06_movieinfo.MovieSource
import pl.training.workshop.exercises.e06_movieinfo.OmdbLikeClient
import pl.training.workshop.exercises.e06_movieinfo.User

/**
 * Start: serwis zna wszystko naraz - surowy format odpowiedzi OMDb ("155 min", "8.0/10"),
 * konwencje starej tabeli (ocena x10), zasadę "tylko personel", pamięć podręczną i tabelę
 * kategorii wiekowych. Sprawdzenie uprawnień i cache są powtórzone w każdej metodzie
 * publicznej, a nowe źródło danych albo nowa reguła dostępu oznacza edycję tej klasy.
 */
class MovieInfoService(private val omdb: OmdbLikeClient, private val legacy: LegacyMovieRows) {
    private val cache = HashMap<String, MovieDetails>()

    fun lookup(user: User, source: MovieSource, id: String): MovieDetails {
        if (!user.staff) {
            throw IllegalStateException("brak uprawnien: ${user.name}")
        }
        val key = "$source:$id"
        if (key in cache) {
            return cache.getValue(key)
        }
        val details = fetch(source, id)
        cache[key] = details
        return details
    }

    fun runtime(user: User, source: MovieSource, id: String): Int {
        if (!user.staff) {
            throw IllegalStateException("brak uprawnien: ${user.name}")
        }
        val key = "$source:$id"
        if (key in cache) {
            return cache.getValue(key).runtimeMinutes
        }
        return fetch(source, id).runtimeMinutes
    }

    private fun fetch(source: MovieSource, id: String): MovieDetails {
        if (source == MovieSource.OMDB) {
            val raw = omdb.byId(id)
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
        val row = legacy.find(id) ?: throw IllegalArgumentException("unknown movie: $id")
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
